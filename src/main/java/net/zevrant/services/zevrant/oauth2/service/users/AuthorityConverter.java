package net.zevrant.services.zevrant.oauth2.service.users;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class AuthorityConverter implements Converter<String, GrantedAuthority> {
    @Override
    public GrantedAuthority convert(String source) {
        return ZevrantsAuthorities.valueOf(source);
    }
}
