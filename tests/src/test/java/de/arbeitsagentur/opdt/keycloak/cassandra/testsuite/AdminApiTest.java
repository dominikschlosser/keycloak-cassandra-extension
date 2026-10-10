package de.arbeitsagentur.opdt.keycloak.cassandra.testsuite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import de.arbeitsagentur.opdt.keycloak.cassandra.testsuite.cassandra.CassandraKeycloakServerConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.keycloak.testframework.annotations.InjectAdminClient;
import org.keycloak.testframework.annotations.InjectKeycloakUrls;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.config.Config;
import org.keycloak.testframework.server.KeycloakUrls;

@KeycloakIntegrationTest(config = CassandraKeycloakServerConfig.class)
class AdminApiTest {

    @InjectAdminClient
    Keycloak adminClient;

    @InjectKeycloakUrls
    KeycloakUrls keycloakUrls;

    @Test
    void bootstrapAdminUserCanLogInWithPassword() {
        try (Keycloak userClient = Keycloak.getInstance(
                keycloakUrls.getBase(), "master", Config.getAdminUsername(), Config.getAdminPassword(), "admin-cli")) {
            assertThat(realmNames(userClient), hasItem("master"));
        }
    }

    @Test
    void realmWithClientRolesCanBeDeleted() {
        RoleRepresentation clientRole = new RoleRepresentation();
        clientRole.setName("client-role");
        RolesRepresentation roles = new RolesRepresentation();
        roles.setClient(Map.of("role-client", List.of(clientRole)));

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("role-client");

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm("realm-to-delete");
        realm.setEnabled(true);
        realm.setClients(List.of(client));
        realm.setRoles(roles);
        adminClient.realms().create(realm);
        assertThat(realmNames(adminClient), hasItem("realm-to-delete"));

        adminClient.realm("realm-to-delete").remove();

        assertThat(realmNames(adminClient), not(hasItem("realm-to-delete")));
    }

    private static List<String> realmNames(Keycloak client) {
        return client.realms().findAll().stream()
                .map(RealmRepresentation::getRealm)
                .toList();
    }
}
