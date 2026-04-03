package de.arbeitsagentur.opdt.keycloak.cassandra.clientScope.persistence.entities;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import de.arbeitsagentur.opdt.keycloak.cassandra.clientScope.CassandraClientScopeAdapter;
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
@CqlName("client_scopes")
public class ClientScopes implements TransactionalEntity, HasAttributes {
    @PartitionKey
    private String realmId;

    private Long version;

    @Builder.Default
    private Set<ClientScopeValue> clientScopes = new HashSet<>();

    @Override
    public String getId() {
        return realmId;
    }

    public Set<ClientScopeValue> getClientScopes() {
        if (clientScopes == null) {
            clientScopes = new HashSet<>();
        }
        return clientScopes;
    }

    public ClientScopeValue getClientScopeById(String id) {
        return getClientScopes().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<ClientScopeValue> getClientScopesByProtocol(String protocol) {
        return getClientScopes().stream()
                .filter(s -> Objects.equals(s.getFirstAttribute(CassandraClientScopeAdapter.PROTOCOL), protocol))
                .collect(Collectors.toList());
    }

    public void addClientScope(ClientScopeValue clientScopeValue) {
        getClientScopes().add(clientScopeValue);
    }

    public boolean removeClientScope(String id) {
        return getClientScopes().removeIf(s -> Objects.equals(s.getId(), id));
    }

    public Map<String, List<String>> getAttributes() {
        return Collections.emptyMap();
    }
}
