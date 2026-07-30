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
package de.arbeitsagentur.opdt.keycloak.cassandra.group;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Map;
import java.util.stream.Stream;
import org.keycloak.models.GroupModel;
import org.keycloak.models.GroupModel.Type;
import org.keycloak.models.GroupProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;

public class RoutedGroupProvider extends ConditionalAreaRouter<GroupProvider> implements GroupProvider {

    public RoutedGroupProvider(KeycloakSession session, GroupProvider cassandra, String cassandraFactoryId) {
        super(session, Area.GROUP, GroupProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public Stream<GroupModel> getGroupsStream(RealmModel realm) {
        return select(realm).getGroupsStream(realm);
    }

    @Override
    public Stream<GroupModel> getGroupsStream(RealmModel realm, Stream<String> ids) {
        return select(realm).getGroupsStream(realm, ids);
    }

    @Override
    public Stream<GroupModel> getGroupsStream(
            RealmModel realm, Stream<String> ids, String search, Integer first, Integer max) {
        return select(realm).getGroupsStream(realm, ids, search, first, max);
    }

    @Override
    public Stream<GroupModel> getGroupsStream(RealmModel realm, Stream<String> ids, Integer first, Integer max) {
        return select(realm).getGroupsStream(realm, ids, first, max);
    }

    @Override
    public Long getGroupsCount(RealmModel realm, Stream<String> ids, String search) {
        return select(realm).getGroupsCount(realm, ids, search);
    }

    @Override
    public Long getGroupsCount(RealmModel realm, Boolean onlyTopGroups) {
        return select(realm).getGroupsCount(realm, onlyTopGroups);
    }

    @Override
    public Long getGroupsCountByNameContaining(RealmModel realm, String search) {
        return select(realm).getGroupsCountByNameContaining(realm, search);
    }

    @Override
    public Stream<GroupModel> getGroupsByRoleStream(
            RealmModel realm, RoleModel role, Integer firstResult, Integer maxResults) {
        return select(realm).getGroupsByRoleStream(realm, role, firstResult, maxResults);
    }

    @Override
    public Stream<GroupModel> getTopLevelGroupsStream(RealmModel realm) {
        return select(realm).getTopLevelGroupsStream(realm);
    }

    @Override
    public Stream<GroupModel> getTopLevelGroupsStream(RealmModel realm, Integer firstResult, Integer maxResults) {
        return select(realm).getTopLevelGroupsStream(realm, firstResult, maxResults);
    }

    @Override
    public Stream<GroupModel> getTopLevelGroupsStream(
            RealmModel realm, String search, Boolean exact, Integer firstResult, Integer maxResults) {
        return select(realm).getTopLevelGroupsStream(realm, search, exact, firstResult, maxResults);
    }

    @Override
    public GroupModel createGroup(RealmModel realm, String name) {
        return select(realm).createGroup(realm, name);
    }

    @Override
    public GroupModel createGroup(RealmModel realm, String id, String name) {
        return select(realm).createGroup(realm, id, name);
    }

    @Override
    public GroupModel createGroup(RealmModel realm, String name, GroupModel toParent) {
        return select(realm).createGroup(realm, name, toParent);
    }

    @Override
    public GroupModel createGroup(RealmModel realm, String id, String name, GroupModel toParent) {
        return select(realm).createGroup(realm, id, name, toParent);
    }

    @Override
    public GroupModel createGroup(RealmModel realm, String id, Type type, String name, GroupModel toParent) {
        return select(realm).createGroup(realm, id, type, name, toParent);
    }

    @Override
    public boolean removeGroup(RealmModel realm, GroupModel group) {
        return select(realm).removeGroup(realm, group);
    }

    @Override
    public void moveGroup(RealmModel realm, GroupModel group, GroupModel toParent) {
        select(realm).moveGroup(realm, group, toParent);
    }

    @Override
    public void addTopLevelGroup(RealmModel realm, GroupModel subGroup) {
        select(realm).addTopLevelGroup(realm, subGroup);
    }

    @Override
    public void preRemove(RealmModel realm) {
        select(realm).preRemove(realm);
    }

    @Override
    public GroupModel getGroupById(RealmModel realm, String id) {
        return select(realm).getGroupById(realm, id);
    }

    @Override
    public GroupModel getGroupByName(RealmModel realm, GroupModel parent, String name) {
        return select(realm).getGroupByName(realm, parent, name);
    }

    @Override
    public Stream<GroupModel> searchForGroupByNameStream(
            RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        return select(realm).searchForGroupByNameStream(realm, search, firstResult, maxResults);
    }

    @Override
    public Stream<GroupModel> searchForGroupByNameStream(
            RealmModel realm, String search, Boolean exact, Integer firstResult, Integer maxResults) {
        return select(realm).searchForGroupByNameStream(realm, search, exact, firstResult, maxResults);
    }

    @Override
    public Stream<GroupModel> searchGroupsByAttributes(
            RealmModel realm, Map<String, String> attributes, Integer firstResult, Integer maxResults) {
        return select(realm).searchGroupsByAttributes(realm, attributes, firstResult, maxResults);
    }
}
