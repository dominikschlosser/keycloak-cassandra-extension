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
package de.arbeitsagentur.opdt.keycloak.cassandra.singleUseObject;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import java.util.Map;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.SingleUseObjectProvider;

public class RoutedSingleUseObjectProvider extends ConditionalAreaRouter<SingleUseObjectProvider>
        implements SingleUseObjectProvider {

    public RoutedSingleUseObjectProvider(
            KeycloakSession session, SingleUseObjectProvider cassandra, String cassandraFactoryId) {
        super(session, Area.SINGLE_USE_OBJECT, SingleUseObjectProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public void put(String key, long lifespanSeconds, Map<String, String> notes) {
        select(null).put(key, lifespanSeconds, notes);
    }

    @Override
    public Map<String, String> get(String key) {
        return select(null).get(key);
    }

    @Override
    public Map<String, String> remove(String key) {
        return select(null).remove(key);
    }

    @Override
    public boolean replace(String key, Map<String, String> notes) {
        return select(null).replace(key, notes);
    }

    @Override
    public boolean putIfAbsent(String key, long lifespanInSeconds) {
        return select(null).putIfAbsent(key, lifespanInSeconds);
    }

    @Override
    public boolean contains(String key) {
        return select(null).contains(key);
    }
}
