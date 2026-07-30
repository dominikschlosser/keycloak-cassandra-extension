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
package de.arbeitsagentur.opdt.keycloak.cassandra.role;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Set;
import java.util.stream.Stream;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.RoleProvider;

public class RoutedRoleProvider extends ConditionalAreaRouter<RoleProvider> implements RoleProvider {

    public RoutedRoleProvider(KeycloakSession session, RoleProvider cassandra, String cassandraFactoryId) {
        super(session, Area.ROLE, RoleProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public RoleModel addRealmRole(RealmModel realm, String name) {
        return select(realm).addRealmRole(realm, name);
    }

    @Override
    public RoleModel addRealmRole(RealmModel realm, String id, String name) {
        return select(realm).addRealmRole(realm, id, name);
    }

    @Override
    public Stream<RoleModel> getRealmRolesStream(RealmModel realm) {
        return select(realm).getRealmRolesStream(realm);
    }

    @Override
    public Stream<RoleModel> getRealmRolesStream(RealmModel realm, Integer first, Integer max) {
        return select(realm).getRealmRolesStream(realm, first, max);
    }

    @Override
    public Stream<RoleModel> getRolesStream(
            RealmModel realm, Stream<String> ids, String search, Integer first, Integer max) {
        return select(realm).getRolesStream(realm, ids, search, first, max);
    }

    @Override
    public Stream<RoleModel> getCompositeRolesStream(RealmModel realm, Set<String> parentRoleIds) {
        return select(realm).getCompositeRolesStream(realm, parentRoleIds);
    }

    @Override
    public boolean removeRole(RoleModel role) {
        return select(null).removeRole(role);
    }

    @Override
    public void removeRoles(RealmModel realm) {
        select(realm).removeRoles(realm);
    }

    @Override
    public RoleModel addClientRole(ClientModel client, String name) {
        return select(null).addClientRole(client, name);
    }

    @Override
    public RoleModel addClientRole(ClientModel client, String id, String name) {
        return select(null).addClientRole(client, id, name);
    }

    @Override
    public Stream<RoleModel> getClientRolesStream(ClientModel client) {
        return select(null).getClientRolesStream(client);
    }

    @Override
    public Stream<RoleModel> getClientRolesStream(ClientModel client, Integer first, Integer max) {
        return select(null).getClientRolesStream(client, first, max);
    }

    @Override
    public void removeRoles(ClientModel client) {
        select(null).removeRoles(client);
    }

    @Override
    public RoleModel getRealmRole(RealmModel realm, String name) {
        return select(realm).getRealmRole(realm, name);
    }

    @Override
    public RoleModel getRoleById(RealmModel realm, String id) {
        return select(realm).getRoleById(realm, id);
    }

    @Override
    public Stream<RoleModel> searchForRolesStream(RealmModel realm, String search, Integer first, Integer max) {
        return select(realm).searchForRolesStream(realm, search, first, max);
    }

    @Override
    public RoleModel getClientRole(ClientModel client, String name) {
        return select(null).getClientRole(client, name);
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(ClientModel client, String search, Integer first, Integer max) {
        return select(null).searchForClientRolesStream(client, search, first, max);
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(
            RealmModel realm, Stream<String> ids, String search, Integer first, Integer max) {
        return select(realm).searchForClientRolesStream(realm, ids, search, first, max);
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(
            RealmModel realm, String search, Stream<String> excludedIds, Integer first, Integer max) {
        return select(realm).searchForClientRolesStream(realm, search, excludedIds, first, max);
    }
}
