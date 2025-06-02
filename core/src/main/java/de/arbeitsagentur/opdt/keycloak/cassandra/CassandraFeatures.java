package de.arbeitsagentur.opdt.keycloak.cassandra;

public class CassandraFeatures {
    private static final String ENV_USER_SESSION_LWT = "KC_COMMUNITY_DATASTORE_CASSANDRA_USER_SESSION_LWT_ENABLED";
    private static final String PROP_USER_SESSION_LWT = "kc.community.datastore.cassandra.userSession.lwt.enabled";

    private static final boolean userSessionLwtEnabled;

    static {
        userSessionLwtEnabled = Boolean.parseBoolean(System.getenv(ENV_USER_SESSION_LWT))
                || Boolean.parseBoolean(System.getProperty(PROP_USER_SESSION_LWT));
    }

    public static boolean isUserSessionLwtEnabled() {
        return userSessionLwtEnabled;
    }
}
