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
package de.arbeitsagentur.opdt.keycloak.cassandra.loginFailure;

import de.arbeitsagentur.opdt.keycloak.cassandra.CassandraStoreConfig.Area;
import de.arbeitsagentur.opdt.keycloak.cassandra.ConditionalAreaRouter;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserLoginFailureModel;
import org.keycloak.models.UserLoginFailureProvider;

public class RoutedUserLoginFailureProvider extends ConditionalAreaRouter<UserLoginFailureProvider>
        implements UserLoginFailureProvider {

    public RoutedUserLoginFailureProvider(
            KeycloakSession session, UserLoginFailureProvider cassandra, String cassandraFactoryId) {
        super(session, Area.LOGIN_FAILURE, UserLoginFailureProvider.class, cassandra, cassandraFactoryId);
    }

    @Override
    public UserLoginFailureModel getUserLoginFailure(RealmModel realm, String userId) {
        return select(realm).getUserLoginFailure(realm, userId);
    }

    @Override
    public UserLoginFailureModel addUserLoginFailure(RealmModel realm, String userId) {
        return select(realm).addUserLoginFailure(realm, userId);
    }

    @Override
    public void removeUserLoginFailure(RealmModel realm, String userId) {
        select(realm).removeUserLoginFailure(realm, userId);
    }

    @Override
    public void removeAllUserLoginFailures(RealmModel realm) {
        select(realm).removeAllUserLoginFailures(realm);
    }

    @Override
    public void updateWithLatestRealmSettings(RealmModel realm) {
        select(realm).updateWithLatestRealmSettings(realm);
    }
}
