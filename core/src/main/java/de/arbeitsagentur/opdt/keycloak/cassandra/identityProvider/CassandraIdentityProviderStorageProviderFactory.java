package de.arbeitsagentur.opdt.keycloak.cassandra.identityProvider;

import static de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.isAreaActive;
import static de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.isAreaConditional;

import com.google.auto.service.AutoService;
import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.provider.EnvironmentDependentProviderFactory;

@AutoService(IdentityProviderStorageProviderFactory.class)
public class CassandraIdentityProviderStorageProviderFactory
        implements IdentityProviderStorageProviderFactory<IdentityProviderStorageProvider>,
                EnvironmentDependentProviderFactory {
    @Override
    public boolean isSupported(Config.Scope scope) {
        return isAreaActive(Area.IDENTITY_PROVIDER);
    }

    @Override
    public IdentityProviderStorageProvider create(KeycloakSession session) {
        CassandraIdentityProviderStorageProvider provider = new CassandraIdentityProviderStorageProvider(session);
        return isAreaConditional(Area.IDENTITY_PROVIDER)
                ? new RoutedIdentityProviderStorageProvider(session, provider, getId())
                : provider;
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        ConditionalAreaRouter.requireFallback(
                factory, Area.IDENTITY_PROVIDER, IdentityProviderStorageProvider.class, getId());
    }

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "cassandra";
    }

    @Override
    public int order() {
        return 11;
    } // Infinispan-Order + 1
}
