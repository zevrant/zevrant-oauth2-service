package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private UserProvider userProvider;

    public ResourceServerConfig(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/login", "/register").permitAll().and()
                // default protection for all resources (including /oauth/authorize)
                .authorizeRequests()
                .anyRequest().hasRole("USER");
        // ... more configuration, e.g. for form login
        http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(new ZevrantClientDetailsService(userProvider))
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
