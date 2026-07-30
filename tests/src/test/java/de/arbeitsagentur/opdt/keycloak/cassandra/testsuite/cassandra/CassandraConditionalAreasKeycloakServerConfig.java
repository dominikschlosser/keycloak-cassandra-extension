package de.arbeitsagentur.opdt.keycloak.cassandra.testsuite.cassandra;

import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

/**
 * Cassandra server config with the login-failure area routed per realm. The fallback store is the
 * in-memory {@code DummyLoginFailureProviderFactory} from src/main (packaged into the server via
 * {@code dependencyCurrentProject}).
 */
public class CassandraConditionalAreasKeycloakServerConfig extends CassandraKeycloakServerConfig {

    @Override
    public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder config) {
        return super.configure(config)
                .dependencyCurrentProject()
                .option("spi-datastore--cassandra--conditional-areas", "login-failure")
                // dependencyCurrentProject also packages NullDeviceRepresentationProviderFactory
                // (order 0, ties with the stock provider and would leave the SPI without a default)
                .option("spi-device-representation--provider", "test");
    }
}
