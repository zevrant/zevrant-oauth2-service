package net.zevrant.services.zevrant.oauth2.service.users;

import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;

public class ZevrantClientDetailsService extends ClientDetailsUserDetailsService {


    public ZevrantClientDetailsService(UserProvider clientDetailsService) {
        super(clientDetailsService);
    }
}
