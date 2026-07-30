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
package de.arbeitsagentur.opdt.keycloak.cassandra.revokedToken;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RevokedTokenProvider;

public class RoutedRevokedTokenProvider extends ConditionalAreaRouter<RevokedTokenProvider>
        implements RevokedTokenProvider {

    public RoutedRevokedTokenProvider(
            KeycloakSession session, RevokedTokenProvider cassandra, String cassandraFactoryId) {
        super(session, Area.REVOKED_TOKEN, RevokedTokenProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public boolean put(String id, long lifespanSeconds) {
        return select(null).put(id, lifespanSeconds);
    }

    @Override
    public boolean contains(String id) {
        return select(null).contains(id);
    }
}
