package de.arbeitsagentur.opdt.keycloak.cassandra.testsuite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserLoginFailureModel;
import org.keycloak.models.UserLoginFailureProvider;
import org.keycloak.models.UserLoginFailureProviderFactory;

/**
 * In-memory login-failure store for the conditional-areas tests. It plays the role of the fallback
 * datastore (in production for example the filestore extension) without adding that dependency.
 */
public class DummyLoginFailureProviderFactory implements UserLoginFailureProviderFactory<UserLoginFailureProvider> {

    public static final String PROVIDER_ID = "dummy";

    public static final Map<String, Map<String, UserLoginFailureModel>> STORE = new ConcurrentHashMap<>();

    @Override
    public UserLoginFailureProvider create(KeycloakSession session) {
        return new UserLoginFailureProvider() {
            @Override
            public UserLoginFailureModel getUserLoginFailure(RealmModel realm, String userId) {
                Map<String, UserLoginFailureModel> failures = STORE.get(realm.getId());
                return failures == null ? null : failures.get(userId);
            }

            @Override
            public UserLoginFailureModel addUserLoginFailure(RealmModel realm, String userId) {
                return STORE.computeIfAbsent(realm.getId(), realmId -> new ConcurrentHashMap<>())
                        .computeIfAbsent(userId, DummyLoginFailure::new);
            }

            @Override
            public void removeUserLoginFailure(RealmModel realm, String userId) {
                Map<String, UserLoginFailureModel> failures = STORE.get(realm.getId());
                if (failures != null) {
                    failures.remove(userId);
                }
            }

            @Override
            public void removeAllUserLoginFailures(RealmModel realm) {
                STORE.remove(realm.getId());
            }

            @Override
            public void close() {}
        };
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Below the cassandra factory (order 2) so cassandra stays the default provider. Above the
     * stock providers so the conditional-area router picks this store as the fallback.
     */
    @Override
    public int order() {
        return 1;
    }

    public static class DummyLoginFailure implements UserLoginFailureModel {
        private final String userId;
        private int failedLoginNotBefore;
        private int numFailures;
        private int numTemporaryLockouts;
        private int numSecondaryAuthFailures;
        private long lastFailure;
        private String lastIPFailure;

        public DummyLoginFailure(String userId) {
            this.userId = userId;
        }

        @Override
        public String getId() {
            return userId;
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public int getFailedLoginNotBefore() {
            return failedLoginNotBefore;
        }

        @Override
        public void setFailedLoginNotBefore(int notBefore) {
            this.failedLoginNotBefore = notBefore;
        }

        @Override
        public int getNumFailures() {
            return numFailures;
        }

        @Override
        public void incrementFailures() {
            numFailures++;
        }

        @Override
        public int getNumTemporaryLockouts() {
            return numTemporaryLockouts;
        }

        @Override
        public void incrementTemporaryLockouts() {
            numTemporaryLockouts++;
        }

        @Override
        public void clearFailures() {
            failedLoginNotBefore = 0;
            numFailures = 0;
            numTemporaryLockouts = 0;
            lastFailure = 0;
            lastIPFailure = null;
        }

        @Override
        public long getLastFailure() {
            return lastFailure;
        }

        @Override
        public void setLastFailure(long lastFailure) {
            this.lastFailure = lastFailure;
        }

        @Override
        public String getLastIPFailure() {
            return lastIPFailure;
        }

        @Override
        public void setLastIPFailure(String ip) {
            this.lastIPFailure = ip;
        }

        @Override
        public int getNumSecondaryAuthFailures() {
            return numSecondaryAuthFailures;
        }

        @Override
        public void incrementSecondaryAuthFailures() {
            numSecondaryAuthFailures++;
        }

        @Override
        public void clearPrimaryAndSecondaryAuthFailures() {
            numFailures = 0;
            numSecondaryAuthFailures = 0;
        }
    }
}
