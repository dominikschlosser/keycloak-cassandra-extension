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
package de.arbeitsagentur.opdt.keycloak.cassandra.userSession;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.UserSessionProvider;

public class RoutedUserSessionProvider extends ConditionalAreaRouter<UserSessionProvider>
        implements UserSessionProvider {

    public RoutedUserSessionProvider(
            KeycloakSession session, UserSessionProvider cassandra, String cassandraFactoryId) {
        super(session, Area.USER_SESSION, UserSessionProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public KeycloakSession getKeycloakSession() {
        return select(null).getKeycloakSession();
    }

    @Override
    public AuthenticatedClientSessionModel createClientSession(
            RealmModel realm, ClientModel client, UserSessionModel userSession) {
        return select(realm).createClientSession(realm, client, userSession);
    }

    @Override
    public AuthenticatedClientSessionModel getClientSession(
            UserSessionModel userSession, ClientModel client, UUID clientSessionId, boolean offline) {
        return select(null).getClientSession(userSession, client, clientSessionId, offline);
    }

    @Override
    public AuthenticatedClientSessionModel getClientSession(
            UserSessionModel userSession, ClientModel client, String clientSessionId, boolean offline) {
        return select(null).getClientSession(userSession, client, clientSessionId, offline);
    }

    @Override
    public AuthenticatedClientSessionModel getClientSession(
            UserSessionModel userSession, ClientModel client, boolean offline) {
        return select(null).getClientSession(userSession, client, offline);
    }

    @Override
    public UserSessionModel createUserSession(
            RealmModel realm,
            UserModel user,
            String loginUsername,
            String ipAddress,
            String authMethod,
            boolean rememberMe,
            String brokerSessionId,
            String brokerUserId) {
        return select(realm)
                .createUserSession(
                        realm, user, loginUsername, ipAddress, authMethod, rememberMe, brokerSessionId, brokerUserId);
    }

    @Override
    public UserSessionModel createUserSession(
            String id,
            RealmModel realm,
            UserModel user,
            String loginUsername,
            String ipAddress,
            String authMethod,
            boolean rememberMe,
            String brokerSessionId,
            String brokerUserId,
            UserSessionModel.SessionPersistenceState persistenceState) {
        return select(realm)
                .createUserSession(
                        id,
                        realm,
                        user,
                        loginUsername,
                        ipAddress,
                        authMethod,
                        rememberMe,
                        brokerSessionId,
                        brokerUserId,
                        persistenceState);
    }

    @Override
    public UserSessionModel getUserSession(RealmModel realm, String id) {
        return select(realm).getUserSession(realm, id);
    }

    @Override
    public Stream<UserSessionModel> getUserSessionsStream(RealmModel realm, UserModel user) {
        return select(realm).getUserSessionsStream(realm, user);
    }

    @Override
    public Stream<UserSessionModel> getUserSessionsStream(RealmModel realm, ClientModel client) {
        return select(realm).getUserSessionsStream(realm, client);
    }

    @Override
    public Stream<UserSessionModel> getUserSessionsStream(
            RealmModel realm, ClientModel client, Integer firstResult, Integer maxResults) {
        return select(realm).getUserSessionsStream(realm, client, firstResult, maxResults);
    }

    @Override
    public Stream<UserSessionModel> getUserSessionByBrokerUserIdStream(RealmModel realm, String brokerUserId) {
        return select(realm).getUserSessionByBrokerUserIdStream(realm, brokerUserId);
    }

    @Override
    public UserSessionModel getUserSessionByBrokerSessionId(RealmModel realm, String brokerSessionId) {
        return select(realm).getUserSessionByBrokerSessionId(realm, brokerSessionId);
    }

    @Override
    public UserSessionModel getUserSessionWithPredicate(
            RealmModel realm, String id, boolean offline, Predicate<UserSessionModel> predicate) {
        return select(realm).getUserSessionWithPredicate(realm, id, offline, predicate);
    }

    @Override
    public long getActiveUserSessions(RealmModel realm, ClientModel client) {
        return select(realm).getActiveUserSessions(realm, client);
    }

    @Override
    public Map<String, Long> getActiveClientSessionStats(RealmModel realm, boolean offline) {
        return select(realm).getActiveClientSessionStats(realm, offline);
    }

    @Override
    public void removeUserSession(RealmModel realm, UserSessionModel session) {
        select(realm).removeUserSession(realm, session);
    }

    @Override
    public void removeUserSessions(RealmModel realm, UserModel user) {
        select(realm).removeUserSessions(realm, user);
    }

    @Override
    public void removeAllExpired() {
        cassandra().removeAllExpired();
        fallback().removeAllExpired();
    }

    @Override
    public void removeExpired(RealmModel realm) {
        select(realm).removeExpired(realm);
    }

    @Override
    public void removeUserSessions(RealmModel realm) {
        select(realm).removeUserSessions(realm);
    }

    @Override
    public void onRealmRemoved(RealmModel realm) {
        select(realm).onRealmRemoved(realm);
    }

    @Override
    public void onClientRemoved(RealmModel realm, ClientModel client) {
        select(realm).onClientRemoved(realm, client);
    }

    @Override
    public UserSessionModel createOfflineUserSession(UserSessionModel userSession) {
        return select(null).createOfflineUserSession(userSession);
    }

    @Override
    public UserSessionModel getOfflineUserSession(RealmModel realm, String userSessionId) {
        return select(realm).getOfflineUserSession(realm, userSessionId);
    }

    @Override
    public void removeOfflineUserSession(RealmModel realm, UserSessionModel userSession) {
        select(realm).removeOfflineUserSession(realm, userSession);
    }

    @Override
    public AuthenticatedClientSessionModel createOfflineClientSession(
            AuthenticatedClientSessionModel clientSession, UserSessionModel offlineUserSession) {
        return select(null).createOfflineClientSession(clientSession, offlineUserSession);
    }

    @Override
    public Stream<UserSessionModel> getOfflineUserSessionsStream(RealmModel realm, UserModel user) {
        return select(realm).getOfflineUserSessionsStream(realm, user);
    }

    @Override
    public Stream<UserSessionModel> getOfflineUserSessionByBrokerUserIdStream(RealmModel realm, String brokerUserId) {
        return select(realm).getOfflineUserSessionByBrokerUserIdStream(realm, brokerUserId);
    }

    @Override
    public long getOfflineSessionsCount(RealmModel realm, ClientModel client) {
        return select(realm).getOfflineSessionsCount(realm, client);
    }

    @Override
    public Stream<UserSessionModel> getOfflineUserSessionsStream(
            RealmModel realm, ClientModel client, Integer firstResult, Integer maxResults) {
        return select(realm).getOfflineUserSessionsStream(realm, client, firstResult, maxResults);
    }

    @Override
    public void importUserSessions(Collection<UserSessionModel> persistentUserSessions, boolean offline) {
        select(null).importUserSessions(persistentUserSessions, offline);
    }

    @Override
    public int getStartupTime(RealmModel realm) {
        return select(realm).getStartupTime(realm);
    }

    @Override
    public void migrate(String modelVersion) {
        select(null).migrate(modelVersion);
    }

    @Override
    public UserSessionModel getUserSessionIfClientExists(
            RealmModel realm, String userSessionId, boolean offline, String clientUUID) {
        return select(realm).getUserSessionIfClientExists(realm, userSessionId, offline, clientUUID);
    }

    @Override
    public Stream<UserSessionModel> readOnlyStreamUserSessions(RealmModel realm) {
        return select(realm).readOnlyStreamUserSessions(realm);
    }

    @Override
    public Stream<UserSessionModel> readOnlyStreamOfflineUserSessions(RealmModel realm) {
        return select(realm).readOnlyStreamOfflineUserSessions(realm);
    }

    @Override
    public Stream<UserSessionModel> readOnlyStreamUserSessions(
            RealmModel realm, ClientModel client, int skip, int maxResults) {
        return select(realm).readOnlyStreamUserSessions(realm, client, skip, maxResults);
    }

    @Override
    public Stream<UserSessionModel> readOnlyStreamOfflineUserSessions(
            RealmModel realm, ClientModel client, int skip, int maxResults) {
        return select(realm).readOnlyStreamOfflineUserSessions(realm, client, skip, maxResults);
    }
}
