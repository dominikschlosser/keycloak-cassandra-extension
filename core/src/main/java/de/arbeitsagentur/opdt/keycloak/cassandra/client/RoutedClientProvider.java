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
package de.arbeitsagentur.opdt.keycloak.cassandra.client;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RoutedClientProvider extends ConditionalAreaRouter<ClientProvider> implements ClientProvider {

    public RoutedClientProvider(KeycloakSession session, ClientProvider cassandra, String cassandraFactoryId) {
        super(session, Area.CLIENT, ClientProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public Stream<ClientModel> getClientsStream(RealmModel realm) {
        return select(realm).getClientsStream(realm);
    }

    @Override
    public Stream<ClientModel> getClientsStream(RealmModel realm, Integer firstResult, Integer maxResults) {
        return select(realm).getClientsStream(realm, firstResult, maxResults);
    }

    @Override
    public ClientModel addClient(RealmModel realm, String clientId) {
        return select(realm).addClient(realm, clientId);
    }

    @Override
    public ClientModel addClient(RealmModel realm, String id, String clientId) {
        return select(realm).addClient(realm, id, clientId);
    }

    @Override
    public long getClientsCount(RealmModel realm) {
        return select(realm).getClientsCount(realm);
    }

    @Override
    public Stream<ClientModel> getAlwaysDisplayInConsoleClientsStream(RealmModel realm) {
        return select(realm).getAlwaysDisplayInConsoleClientsStream(realm);
    }

    @Override
    public boolean removeClient(RealmModel realm, String id) {
        return select(realm).removeClient(realm, id);
    }

    @Override
    public void removeClients(RealmModel realm) {
        select(realm).removeClients(realm);
    }

    @Override
    public void addClientScopes(
            RealmModel realm, ClientModel client, Set<ClientScopeModel> clientScopes, boolean defaultScope) {
        select(realm).addClientScopes(realm, client, clientScopes, defaultScope);
    }

    @Override
    public void removeClientScope(RealmModel realm, ClientModel client, ClientScopeModel clientScope) {
        select(realm).removeClientScope(realm, client, clientScope);
    }

    @Override
    public void addClientScopeToAllClients(RealmModel realm, ClientScopeModel clientScope, boolean defaultClientScope) {
        select(realm).addClientScopeToAllClients(realm, clientScope, defaultClientScope);
    }

    @Override
    public Map<ClientModel, Set<String>> getAllRedirectUrisOfEnabledClients(RealmModel realm) {
        return select(realm).getAllRedirectUrisOfEnabledClients(realm);
    }

    @Override
    public ClientModel getClientById(RealmModel realm, String id) {
        return select(realm).getClientById(realm, id);
    }

    @Override
    public ClientModel getClientByClientId(RealmModel realm, String clientId) {
        return select(realm).getClientByClientId(realm, clientId);
    }

    @Override
    public Stream<ClientModel> searchClientsByClientIdStream(
            RealmModel realm, String clientId, Integer firstResult, Integer maxResults) {
        return select(realm).searchClientsByClientIdStream(realm, clientId, firstResult, maxResults);
    }

    @Override
    public Stream<ClientModel> searchClientsByAttributes(
            RealmModel realm, Map<String, String> attributes, Integer firstResult, Integer maxResults) {
        return select(realm).searchClientsByAttributes(realm, attributes, firstResult, maxResults);
    }

    @Override
    public Stream<ClientModel> searchClientsByAuthenticationFlowBindingOverrides(
            RealmModel realm, Map<String, String> overrides, Integer firstResult, Integer maxResults) {
        return select(realm)
                .searchClientsByAuthenticationFlowBindingOverrides(realm, overrides, firstResult, maxResults);
    }

    @Override
    public Map<String, ClientScopeModel> getClientScopes(RealmModel realm, ClientModel client, boolean defaultScopes) {
        return select(realm).getClientScopes(realm, client, defaultScopes);
    }
}
