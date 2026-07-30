package de.arbeitsagentur.opdt.keycloak.cassandra.testsuite;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.arbeitsagentur.opdt.keycloak.cassandra.testsuite.cassandra.CassandraConditionalAreasKeycloakServerConfig;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserLoginFailureModel;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.remote.annotations.TestOnServer;

/**
 * End-to-end coverage for conditional areas against a real server. The login-failure area is
 * conditional. The fallback is the in-memory dummy store, so no second datastore extension is
 * needed. Assertions use class names instead of class identity (the server may load the provider
 * classes in its own classloader).
 */
@KeycloakIntegrationTest(config = CassandraConditionalAreasKeycloakServerConfig.class)
public class ConditionalAreaRoutingTest extends CassandraModelTest {

    private static final String OPT_IN_ATTRIBUTE = "datastoreLoginFailureEnabled";

    @TestOnServer
    public void loginFailuresAreRoutedPerRealm(KeycloakSession testSession) {
        String optedId = inCommittedTransaction(testSession, session -> {
            RealmModel realm = session.realms().createRealm("conditional-opted");
            realm.setAttribute(OPT_IN_ATTRIBUTE, "true");
            return realm.getId();
        });
        String fallbackId = inCommittedTransaction(testSession, session -> {
            return session.realms().createRealm("conditional-fallback").getId();
        });

        try {
            inCommittedTransaction(testSession, session -> {
                session.loginFailures().addUserLoginFailure(session.realms().getRealm(optedId), "user-opted");
                session.loginFailures().addUserLoginFailure(session.realms().getRealm(fallbackId), "user-fallback");
            });

            inCommittedTransaction(testSession, session -> {
                UserLoginFailureModel opted = session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(optedId), "user-opted");
                assertNotNull(opted);
                assertTrue(
                        "expected a cassandra model but got " + opted.getClass().getName(),
                        opted.getClass()
                                .getName()
                                .startsWith("de.arbeitsagentur.opdt.keycloak.cassandra.loginFailure"));

                UserLoginFailureModel fallback = session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(fallbackId), "user-fallback");
                assertNotNull(fallback);
                assertTrue(
                        "expected the dummy model but got "
                                + fallback.getClass().getName(),
                        fallback.getClass().getName().contains("DummyLoginFailure"));
            });

            // Flipping the attribute switches the store. Entries of the other store are not visible.
            inCommittedTransaction(testSession, session -> {
                session.realms().getRealm(optedId).removeAttribute(OPT_IN_ATTRIBUTE);
                session.realms().getRealm(fallbackId).setAttribute(OPT_IN_ATTRIBUTE, "true");
            });

            inCommittedTransaction(testSession, session -> {
                assertNull(session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(optedId), "user-opted"));
                assertNull(session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(fallbackId), "user-fallback"));
            });

            // Flipping back makes the original entries visible again (nothing was migrated or deleted).
            inCommittedTransaction(testSession, session -> {
                session.realms().getRealm(optedId).setAttribute(OPT_IN_ATTRIBUTE, "true");
                session.realms().getRealm(fallbackId).removeAttribute(OPT_IN_ATTRIBUTE);
            });

            inCommittedTransaction(testSession, session -> {
                assertNotNull(session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(optedId), "user-opted"));
                assertNotNull(session.loginFailures()
                        .getUserLoginFailure(session.realms().getRealm(fallbackId), "user-fallback"));
            });
        } finally {
            inCommittedTransaction(testSession, session -> {
                session.realms().removeRealm(optedId);
                session.realms().removeRealm(fallbackId);
            });
            DummyLoginFailureProviderFactory.STORE.clear();
        }
    }
}
