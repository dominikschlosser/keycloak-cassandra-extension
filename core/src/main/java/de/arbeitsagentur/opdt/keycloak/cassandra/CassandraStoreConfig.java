/*
 * Copyright 2026 IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.arbeitsagentur.opdt.keycloak.cassandra;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.keycloak.Config;

/**
 * Which storage areas the Cassandra extension serves, mirroring the areas model of the k8store
 * extension. Read once (lazily) from the datastore scope, i.e. {@code
 * --spi-datastore--cassandra--areas=<value>}.
 *
 * <p>The area set is the extension's opt-in and replaces the former {@code CommunityProfiles} env
 * vars ({@code KC_COMMUNITY_DATASTORE_CASSANDRA_ENABLED} / {@code ..._CACHE_ENABLED}). It resolves
 * to:
 *
 * <ul>
 *   <li>the explicit {@code areas} list when set - this is how the extension contributes providers
 *       for the SPIs another selected datastore (e.g. k8store) does not serve;
 *   <li>otherwise, when {@code cassandra} is the selected datastore, every area (the full-datastore
 *       back-compat default);
 *   <li>otherwise the empty set - the jar is on the classpath but dormant, so it never shadows a
 *       deployment that did not ask for it.
 * </ul>
 *
 * <p>An enabled area makes the matching Cassandra provider factory {@code isSupported}; combined
 * with its higher {@code order()} that provider becomes the default for its SPI, so it is resolved
 * both directly ({@code session.getProvider(X.class)}) and through another datastore's fall-through
 * ({@code super.users()} etc.). A disabled area leaves the built-in jpa/infinispan provider in
 * place.
 *
 * <p>{@code --spi-datastore--cassandra--conditional-areas} additionally routes areas per realm. The
 * factory registers as usual, but hands out a {@code Routed*Provider} wrapper (see {@link
 * ConditionalAreaRouter}) that serves a realm from cassandra only when the realm has the opt-in
 * attribute of the area (e.g. {@code datastoreClientEnabled=true}). Otherwise it delegates to the
 * highest-order non-cassandra provider of the SPI. Every area except {@code realm} itself can be
 * conditional.
 */
public final class CassandraStoreConfig {

    public static final String DATASTORE_PROVIDER_ID = "cassandra";

    public enum Area {
        REALM("realm", false),
        CLIENT("client", false),
        CLIENT_SCOPE("client-scope", false),
        ROLE("role", false),
        GROUP("group", false),
        IDENTITY_PROVIDER("identity-provider", false),
        USER("user", false),
        USER_SESSION("user-session", true),
        AUTH_SESSION("auth-session", true),
        LOGIN_FAILURE("login-failure", true),
        SINGLE_USE_OBJECT("single-use-object", true),
        REVOKED_TOKEN("revoked-token", true);

        private final String configName;
        private final boolean dynamic;

        Area(String configName, boolean dynamic) {
            this.configName = configName;
            this.dynamic = dynamic;
        }

        /** True for volatile per-login areas (sessions, login failures, single-use, revoked tokens). */
        public boolean isDynamic() {
            return dynamic;
        }

        /**
         * The realm attribute that opts a realm into cassandra when this area is conditional, e.g.
         * {@code datastoreClientEnabled} for {@code client}.
         */
        public String conditionalRealmAttribute() {
            StringBuilder name = new StringBuilder("datastore");
            for (String part : configName.split("-")) {
                name.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
            return name.append("Enabled").toString();
        }

        static Area fromConfigName(String name) {
            String normalized = name.trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(a -> a.configName.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown cassandra area '" + name
                            + "', supported: "
                            + Arrays.stream(values()).map(a -> a.configName).collect(Collectors.joining(", "))
                            + " (or the shorthands 'all' and 'cache')"));
        }
    }

    private static volatile CassandraStoreConfig instance;

    private final Set<Area> areas;
    private final Set<Area> conditionalAreas;

    private CassandraStoreConfig(Set<Area> areas, Set<Area> conditionalAreas) {
        Set<Area> overlap = copy(areas);
        overlap.retainAll(conditionalAreas);
        if (!overlap.isEmpty()) {
            throw new IllegalArgumentException("Areas cannot be both unconditional and conditional: "
                    + overlap.stream().map(a -> a.configName).collect(Collectors.joining(", ")));
        }
        this.areas = copy(areas);
        this.conditionalAreas = copy(conditionalAreas);
    }

    private static Set<Area> copy(Set<Area> areas) {
        return areas.isEmpty() ? EnumSet.noneOf(Area.class) : EnumSet.copyOf(areas);
    }

    /** The dynamic areas - the {@code cache} shorthand, i.e. Cassandra as a cache in front of JPA. */
    public static Set<Area> dynamicAreas() {
        return Arrays.stream(Area.values())
                .filter(Area::isDynamic)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Area.class)));
    }

    /**
     * Area-set grammar of the {@code areas} option: {@code all} = every area, {@code cache} = the
     * dynamic areas, otherwise a comma-separated list of area config names.
     */
    static Set<Area> parseAreas(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("all".equals(normalized)) {
            return EnumSet.allOf(Area.class);
        }
        if ("cache".equals(normalized)) {
            return dynamicAreas();
        }
        Set<Area> areas = EnumSet.noneOf(Area.class);
        for (String area : value.split(",")) {
            areas.add(Area.fromConfigName(area));
        }
        return areas;
    }

    /**
     * Same grammar as {@code areas}, but the realm area itself cannot be conditional. {@code all}
     * therefore means every area except {@code realm}.
     */
    static Set<Area> parseConditionalAreas(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        Set<Area> areas;
        if ("all".equals(normalized)) {
            areas = EnumSet.allOf(Area.class);
            areas.remove(Area.REALM);
        } else {
            areas = parseAreas(value);
            if (areas.contains(Area.REALM)) {
                throw new IllegalArgumentException(
                        "The realm area cannot be conditional (the opt-in attribute lives on the realm itself)");
            }
        }
        return areas;
    }

    private static CassandraStoreConfig resolve(Config.Scope scope) {
        String conditionalValue = scope == null ? null : scope.get("conditional-areas");
        Set<Area> conditionalAreas = conditionalValue != null && !conditionalValue.isBlank()
                ? parseConditionalAreas(conditionalValue)
                : EnumSet.noneOf(Area.class);

        String value = scope == null ? null : scope.get("areas");
        Set<Area> areas;
        if (value != null && !value.isBlank()) {
            areas = parseAreas(value);
        } else if (DATASTORE_PROVIDER_ID.equals(Config.getProvider("datastore"))) {
            areas = EnumSet.allOf(Area.class);
            areas.removeAll(conditionalAreas);
        } else {
            areas = EnumSet.noneOf(Area.class);
        }
        return new CassandraStoreConfig(areas, conditionalAreas);
    }

    public static CassandraStoreConfig get() {
        CassandraStoreConfig config = instance;
        if (config == null) {
            synchronized (CassandraStoreConfig.class) {
                if (instance == null) {
                    instance = resolve(Config.scope("datastore", "cassandra"));
                }
                config = instance;
            }
        }
        return config;
    }

    /** Programmatic configuration for tests. */
    public static CassandraStoreConfig of(Set<Area> areas) {
        return of(areas, EnumSet.noneOf(Area.class));
    }

    /** Programmatic configuration for tests. */
    public static CassandraStoreConfig of(Set<Area> areas, Set<Area> conditionalAreas) {
        CassandraStoreConfig config = new CassandraStoreConfig(areas, conditionalAreas);
        instance = config;
        return config;
    }

    /** Re-read on the next {@link #get()}; embedded test runs reuse the JVM across servers. */
    public static void reset() {
        instance = null;
    }

    /** True when the area is unconditionally cassandra-backed. */
    public static boolean isAreaEnabled(Area area) {
        return get().areas.contains(area);
    }

    /** True when the area is cassandra-backed only for realms carrying the opt-in attribute. */
    public static boolean isAreaConditional(Area area) {
        return get().conditionalAreas.contains(area);
    }

    /** True when the cassandra provider factory of the area must be registered at all. */
    public static boolean isAreaActive(Area area) {
        return isAreaEnabled(area) || isAreaConditional(area);
    }

    public Set<Area> getAreas() {
        return copy(areas);
    }

    public Set<Area> getConditionalAreas() {
        return copy(conditionalAreas);
    }
}
