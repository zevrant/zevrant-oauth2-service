package net.zevrant.services.zevrant.oauth2.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;

public class AuthenticationManager extends OAuth2AuthenticationManager {

    public AuthenticationManager(ResourceServerTokenServices tokenServices) {
        this.setTokenService(tokenServices);
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        super.setClientDetailsService(clientDetailsService);
    }

    public void setTokenService(ResourceServerTokenServices tokenService) {
        super.setTokenServices(tokenService);
    }

}
