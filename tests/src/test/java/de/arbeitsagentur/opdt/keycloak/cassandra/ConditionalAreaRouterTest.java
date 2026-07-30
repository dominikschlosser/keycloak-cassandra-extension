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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.InvalidationHandler.InvalidableObjectType;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.provider.ProviderEventListener;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

/**
 * Startup validation of the conditional areas. A misconfigured server must fail to boot, which the
 * integration tests cannot cover; the routing behavior itself is covered end-to-end by {@code
 * ConditionalAreaRoutingTest}.
 */
class ConditionalAreaRouterTest {

    @BeforeEach
    void setUp() {
        CassandraStoreConfig.of(EnumSet.noneOf(Area.class), EnumSet.of(Area.CLIENT));
    }

    @AfterEach
    void tearDown() {
        CassandraStoreConfig.reset();
    }

    @Test
    void startupValidationPassesWhenAFallbackIsRegistered() {
        ConditionalAreaRouter.requireFallback(
                new StubSessionFactory(new StubProviderFactory("cassandra", 100), new StubProviderFactory("jpa", 0)),
                Area.CLIENT,
                ClientProvider.class,
                "cassandra");
    }

    @Test
    void startupValidationFailsWhenOnlyTheCassandraFactoryIsRegistered() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class,
                () -> ConditionalAreaRouter.requireFallback(
                        new StubSessionFactory(new StubProviderFactory("cassandra", 100)),
                        Area.CLIENT,
                        ClientProvider.class,
                        "cassandra"));
        assertTrue(e.getMessage().contains("datastoreClientEnabled"));
    }

    @Test
    void startupValidationMentionsTheShadowedInfinispanProvider() {
        IllegalStateException e = assertThrows(
                IllegalStateException.class,
                () -> ConditionalAreaRouter.requireFallback(
                        new StubSessionFactory(new StubProviderFactory("infinispan", 100)),
                        Area.CLIENT,
                        ClientProvider.class,
                        "infinispan"));
        assertTrue(e.getMessage().contains("infinispan cannot be the fallback"));
    }

    @Test
    void startupValidationIgnoresNonConditionalAreas() {
        ConditionalAreaRouter.requireFallback(new StubSessionFactory(), Area.USER, ClientProvider.class, "cassandra");
    }

    private static final class StubProviderFactory implements ProviderFactory<Provider> {
        private final String id;
        private final int order;

        StubProviderFactory(String id, int order) {
            this.id = id;
            this.order = order;
        }

        @Override
        public Provider create(KeycloakSession session) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void init(Config.Scope config) {}

        @Override
        public void postInit(KeycloakSessionFactory factory) {}

        @Override
        public void close() {}

        @Override
        public String getId() {
            return id;
        }

        @Override
        public int order() {
            return order;
        }
    }

    @SuppressWarnings("rawtypes")
    private static final class StubSessionFactory implements KeycloakSessionFactory {
        private final List<ProviderFactory> factories;

        StubSessionFactory(ProviderFactory... factories) {
            this.factories = List.of(factories);
        }

        @Override
        public Stream<ProviderFactory> getProviderFactoriesStream(Class<? extends Provider> clazz) {
            return factories.stream();
        }

        @Override
        public KeycloakSession create() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Spi> getSpis() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Spi getSpi(Class<? extends Provider> providerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Provider> ProviderFactory<T> getProviderFactory(Class<T> clazz) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Provider> ProviderFactory<T> getProviderFactory(Class<T> clazz, String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Provider> ProviderFactory<T> getProviderFactory(
                Class<T> clazz,
                String realmId,
                String componentId,
                Function<KeycloakSessionFactory, ComponentModel> modelGetter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getServerStartupTimestamp() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void register(ProviderEventListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unregister(ProviderEventListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void publish(ProviderEvent event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate(KeycloakSession session, InvalidableObjectType type, Object... params) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {}
    }
}
