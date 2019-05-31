package net.zevrant.services.zevrant.oauth2.service.users;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Service
@ConfigurationProperties(prefix = "zevrant.oauth.clients", ignoreUnknownFields = false)
public class UserProvider extends InMemoryClientDetailsService {

    public void setUsers(List<ZevrantsClientDetails> users) {
        super.setClientDetailsStore(createUsers(users));
    }

    public Map<String, ZevrantsClientDetails> createUsers(List<? extends ClientDetails> users) {
        Map<String, ZevrantsClientDetails> clientDetailsStore = new HashMap<>();
        users.stream().forEach((user) -> ((ZevrantsClientDetails) user).setClientSecret("{noop}".concat(user.getClientSecret())));
        users.stream().forEach((user) -> clientDetailsStore.put(user.getClientId(), (ZevrantsClientDetails) user));
        return clientDetailsStore;
    }
}
