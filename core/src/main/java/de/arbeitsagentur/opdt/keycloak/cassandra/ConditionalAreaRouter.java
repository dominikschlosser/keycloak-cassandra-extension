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

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import java.util.Comparator;
import java.util.Optional;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;

/**
 * Base class of the per-realm routing providers for {@link CassandraStoreConfig}'s conditional
 * areas. The cassandra factory stays the default provider of its SPI (id and order as usual), but
 * hands out a {@code Routed*Provider} wrapper that picks a store per call via {@link #select}:
 * cassandra when the realm has the opt-in attribute of the area ({@link
 * Area#conditionalRealmAttribute()}), otherwise the highest-order non-cassandra provider of the SPI
 * (the one that would have been default without this extension).
 */
public abstract class ConditionalAreaRouter<T extends Provider> implements ConditionallyRouted {

    private final KeycloakSession session;
    private final Area area;
    private final Class<T> spi;
    private final T cassandra;
    private final String cassandraFactoryId;
    private T fallback;

    /**
     * {@code cassandraFactoryId} is the id the cassandra factory registers under ({@code cassandra},
     * or {@code infinispan} for the shadowing dynamic areas). It is excluded when resolving the
     * fallback.
     */
    protected ConditionalAreaRouter(
            KeycloakSession session, Area area, Class<T> spi, T cassandra, String cassandraFactoryId) {
        this.session = session;
        this.area = area;
        this.spi = spi;
        this.cassandra = cassandra;
        this.cassandraFactoryId = cassandraFactoryId;
    }

    @Override
    public final Provider cassandraDelegate() {
        return cassandra;
    }

    /**
     * The store serving the given realm. SPI methods without a realm argument pass {@code null},
     * which falls back to the session context realm. Calls with no resolvable realm stay on
     * cassandra.
     */
    protected final T select(RealmModel realm) {
        if (realm == null) {
            realm = session.getContext().getRealm();
        }
        if (realm == null || Boolean.parseBoolean(realm.getAttribute(area.conditionalRealmAttribute()))) {
            return cassandra;
        }
        return fallback();
    }

    /** The cassandra store, for maintenance sweeps that must run on both stores. */
    protected final T cassandra() {
        return cassandra;
    }

    /** The fallback store, resolved lazily on the first call of a non-opted realm. */
    protected final T fallback() {
        if (fallback == null) {
            String id = fallbackId(session.getKeycloakSessionFactory(), spi, cassandraFactoryId)
                    .orElseThrow(() -> missingFallback(area, spi, cassandraFactoryId));
            fallback = session.getProvider(spi, id);
        }
        return fallback;
    }

    /** The fallback, if resolved, was obtained via session.getProvider and is closed by the session itself. */
    public final void close() {
        cassandra.close();
    }

    /**
     * Startup validation, called from the factories' {@code postInit}. A conditional area without a
     * fallback provider fails here instead of on the first call of a non-opted realm.
     */
    public static void requireFallback(
            KeycloakSessionFactory factory, Area area, Class<? extends Provider> spi, String cassandraFactoryId) {
        if (CassandraStoreConfig.isAreaConditional(area)) {
            fallbackId(factory, spi, cassandraFactoryId)
                    .orElseThrow(() -> missingFallback(area, spi, cassandraFactoryId));
        }
    }

    private static Optional<String> fallbackId(
            KeycloakSessionFactory factory, Class<? extends Provider> spi, String cassandraFactoryId) {
        return factory.getProviderFactoriesStream(spi)
                .filter(f -> !cassandraFactoryId.equals(f.getId()))
                .max(Comparator.comparingInt(ProviderFactory::order))
                .map(ProviderFactory::getId);
    }

    private static IllegalStateException missingFallback(
            Area area, Class<? extends Provider> spi, String cassandraFactoryId) {
        String hint = "infinispan".equals(cassandraFactoryId)
                ? " - this area replaces the embedded infinispan provider, so infinispan cannot be the fallback"
                : "";
        return new IllegalStateException("Conditional area '" + area + "' has no fallback provider for "
                + spi.getSimpleName() + " (realms without the '" + area.conditionalRealmAttribute()
                + "' attribute need another datastore serving this area" + hint + ")");
    }
}
