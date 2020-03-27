package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantClientDetailsService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private UserProvider userProvider;
    private ConfigurableApplicationContext context;
    private ZevrantOauthResponseClient responseClient;
    public ResourceServerConfig(UserProvider userProvider, ConfigurableApplicationContext context, ZevrantOauthResponseClient responseClient) {
        this.userProvider = userProvider;
        this.context = context;
        this.responseClient = responseClient;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] openPaths = null;
        List<String> activeProfiles = Arrays.asList(context.getEnvironment().getActiveProfiles());
        if(activeProfiles.contains("prod")) {
            openPaths = new String[]{"/authorize", "/register", "/oauth/token", "/indoctrinate", "/email"};
        } else {
            openPaths = new String[]{"/authorize", "/oauth/token", "/register", "/indoctrinate", "/email"};
        }

        http
                .authorizeRequests().antMatchers(openPaths).permitAll()
            .and().csrf().disable();
        // ... more configuration, e.g. for form login
        http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(new ZevrantClientDetailsService(userProvider))
            .passwordEncoder(NoOpPasswordEncoder.getInstance());


    }


}
