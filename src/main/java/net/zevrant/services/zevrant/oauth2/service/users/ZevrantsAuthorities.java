package net.zevrant.services.zevrant.oauth2.service.users;

import org.springframework.security.core.GrantedAuthority;

public enum ZevrantsAuthorities implements GrantedAuthority {
    DEFAULT;


    @Override
    public String getAuthority() {
        return this.name();
    }

}
