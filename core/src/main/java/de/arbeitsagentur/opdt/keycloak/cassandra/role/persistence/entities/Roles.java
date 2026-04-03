/*
 * Copyright 2022 IT-Systemhaus der Bundesagentur fuer Arbeit
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
package de.arbeitsagentur.opdt.keycloak.cassandra.role.persistence.entities;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import de.arbeitsagentur.opdt.keycloak.cassandra.transaction.HasAttributes;
import de.arbeitsagentur.opdt.keycloak.cassandra.transaction.TransactionalEntity;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;

@EqualsAndHashCode(of = "realmId")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@CqlName("roles")
public class Roles implements TransactionalEntity, HasAttributes {
    @PartitionKey
    private String realmId;

    private Long version;

    @Builder.Default
    private Set<RoleValue> realmRoles = new HashSet<>();

    @Builder.Default
    private Map<String, Set<RoleValue>> clientRoles = new HashMap<>();

    @Override
    public String getId() {
        return realmId;
    }

    public Set<RoleValue> getRealmRoles() {
        if (realmRoles == null) {
            realmRoles = new HashSet<>();
        }
        return realmRoles;
    }

    public Map<String, Set<RoleValue>> getClientRoles() {
        if (clientRoles == null) {
            clientRoles = new HashMap<>();
        }
        return clientRoles;
    }

    public RoleValue getRoleById(String id) {
        RoleValue realmRole = getRealmRoles().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (realmRole == null) {
            return getClientRoles().entrySet().stream()
                    .flatMap(e -> e.getValue().stream().filter(r -> r.getId().equals(id)))
                    .findFirst()
                    .orElse(null);
        }
        return realmRole;
    }

    public void addRealmRole(RoleValue role) {
        getRealmRoles().add(role);
    }

    public List<RoleValue> getRealmRoles(Integer first, Integer max) {
        return getRealmRoles().stream()
                .skip(first == null || first < 0 ? 0 : first)
                .limit(max == null || max < 0 ? Long.MAX_VALUE : max)
                .collect(Collectors.toList());
    }

    public boolean removeClientRole(String clientId, String id) {
        Set<RoleValue> rolesByClient = getClientRoles().get(clientId);
        return rolesByClient != null
                && rolesByClient.remove(RoleValue.builder().id(id).build());
    }

    public boolean removeRealmRole(String id) {
        return getRealmRoles().remove(RoleValue.builder().id(id).build());
    }

    public void addClientRole(String clientId, RoleValue role) {
        Set<RoleValue> concreteClientRoles = getClientRoles().getOrDefault(clientId, new HashSet<>());
        concreteClientRoles.add(role);
        getClientRoles().put(clientId, concreteClientRoles);
    }

    public Collection<RoleValue> getClientRoles(String clientId, Integer first, Integer max) {
        return getClientRoles().getOrDefault(clientId, new HashSet<>()).stream()
                .skip(first == null || first < 0 ? 0 : first)
                .limit(max == null || max < 0 ? Long.MAX_VALUE : max)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return Collections.emptyMap();
    }
}
