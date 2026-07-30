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
package de.arbeitsagentur.opdt.keycloak.cassandra.clientScope;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Map;
import java.util.stream.Stream;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.ClientScopeProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

public class RoutedClientScopeProvider extends ConditionalAreaRouter<ClientScopeProvider>
        implements ClientScopeProvider {

    public RoutedClientScopeProvider(
            KeycloakSession session, ClientScopeProvider cassandra, String cassandraFactoryId) {
        super(session, Area.CLIENT_SCOPE, ClientScopeProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public Stream<ClientScopeModel> getClientScopesStream(RealmModel realm) {
        return select(realm).getClientScopesStream(realm);
    }

    @Override
    public ClientScopeModel addClientScope(RealmModel realm, String name) {
        return select(realm).addClientScope(realm, name);
    }

    @Override
    public ClientScopeModel addClientScope(RealmModel realm, String id, String name) {
        return select(realm).addClientScope(realm, id, name);
    }

    @Override
    public boolean removeClientScope(RealmModel realm, String id) {
        return select(realm).removeClientScope(realm, id);
    }

    @Override
    public void removeClientScopes(RealmModel realm) {
        select(realm).removeClientScopes(realm);
    }

    @Override
    public Stream<ClientScopeModel> getClientScopesByProtocol(RealmModel realm, String protocol) {
        return select(realm).getClientScopesByProtocol(realm, protocol);
    }

    @Override
    public Stream<ClientScopeModel> getClientScopesByAttributes(
            RealmModel realm, Map<String, String> searchMap, boolean useOr) {
        return select(realm).getClientScopesByAttributes(realm, searchMap, useOr);
    }

    @Override
    public ClientScopeModel getClientScopeById(RealmModel realm, String id) {
        return select(realm).getClientScopeById(realm, id);
    }
}
