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
package de.arbeitsagentur.opdt.keycloak.cassandra.identityProvider;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Map;
import java.util.stream.Stream;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.IdentityProviderQuery;
import org.keycloak.models.IdentityProviderStorageProvider;
import org.keycloak.models.KeycloakSession;

public class RoutedIdentityProviderStorageProvider extends ConditionalAreaRouter<IdentityProviderStorageProvider>
        implements IdentityProviderStorageProvider {

    public RoutedIdentityProviderStorageProvider(
            KeycloakSession session, IdentityProviderStorageProvider cassandra, String cassandraFactoryId) {
        super(session, Area.IDENTITY_PROVIDER, IdentityProviderStorageProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public IdentityProviderModel create(IdentityProviderModel model) {
        return select(null).create(model);
    }

    @Override
    public void update(IdentityProviderModel model) {
        select(null).update(model);
    }

    @Override
    public boolean remove(String providerAlias) {
        return select(null).remove(providerAlias);
    }

    @Override
    public void removeAll() {
        select(null).removeAll();
    }

    @Override
    public IdentityProviderModel getById(String internalId) {
        return select(null).getById(internalId);
    }

    @Override
    public IdentityProviderModel getByAlias(String alias) {
        return select(null).getByAlias(alias);
    }

    @Override
    public IdentityProviderModel getByIdOrAlias(String key) {
        return select(null).getByIdOrAlias(key);
    }

    @Override
    public Stream<IdentityProviderModel> getAllStream() {
        return select(null).getAllStream();
    }

    @Override
    public Stream<IdentityProviderModel> getAllStream(IdentityProviderQuery query) {
        return select(null).getAllStream(query);
    }

    @Override
    public Stream<IdentityProviderModel> getAllStream(Map<String, String> options, Integer first, Integer max) {
        return select(null).getAllStream(options, first, max);
    }

    @Override
    public Stream<IdentityProviderModel> getAllStream(IdentityProviderQuery query, Integer first, Integer max) {
        return select(null).getAllStream(query, first, max);
    }

    @Override
    public Stream<IdentityProviderModel> getByOrganization(String orgId, Integer first, Integer max) {
        return select(null).getByOrganization(orgId, first, max);
    }

    @Override
    public Stream<String> getByFlow(String flowId, String search, Integer first, Integer max) {
        return select(null).getByFlow(flowId, search, first, max);
    }

    @Override
    public Stream<IdentityProviderModel> getForLogin(FetchMode mode, String organizationId) {
        return select(null).getForLogin(mode, organizationId);
    }

    @Override
    public long count() {
        return select(null).count();
    }

    @Override
    public boolean isIdentityFederationEnabled() {
        return select(null).isIdentityFederationEnabled();
    }

    @Override
    public IdentityProviderMapperModel createMapper(IdentityProviderMapperModel model) {
        return select(null).createMapper(model);
    }

    @Override
    public void updateMapper(IdentityProviderMapperModel model) {
        select(null).updateMapper(model);
    }

    @Override
    public boolean removeMapper(IdentityProviderMapperModel model) {
        return select(null).removeMapper(model);
    }

    @Override
    public void removeAllMappers() {
        select(null).removeAllMappers();
    }

    @Override
    public IdentityProviderMapperModel getMapperById(String id) {
        return select(null).getMapperById(id);
    }

    @Override
    public IdentityProviderMapperModel getMapperByName(String identityProviderAlias, String name) {
        return select(null).getMapperByName(identityProviderAlias, name);
    }

    @Override
    public Stream<IdentityProviderMapperModel> getMappersStream() {
        return select(null).getMappersStream();
    }

    @Override
    public Stream<IdentityProviderMapperModel> getMappersStream(
            Map<String, String> options, Integer first, Integer max) {
        return select(null).getMappersStream(options, first, max);
    }

    @Override
    public Stream<IdentityProviderMapperModel> getMappersByAliasStream(String identityProviderAlias) {
        return select(null).getMappersByAliasStream(identityProviderAlias);
    }
}
