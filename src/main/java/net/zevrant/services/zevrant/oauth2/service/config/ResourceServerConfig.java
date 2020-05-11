package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantClientDetailsService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private UserProvider userProvider;
    private ConfigurableApplicationContext context;
    private ZevrantOauthResponseClient responseClient;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private String[] openPaths;
    private ClientDetailsService clientDetailsService;
    public ResourceServerConfig(UserProvider userProvider, ConfigurableApplicationContext context, ZevrantOauthResponseClient responseClient,
                                PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService) {
        this.userProvider = userProvider;
        this.context = context;
        this.responseClient = responseClient;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.clientDetailsService = clientDetailsService;
        List<String> activeProfiles = Arrays.asList(context.getEnvironment().getActiveProfiles());
        if (activeProfiles.contains("prod")) {
            openPaths = new String[]{"/authorize", "/token", "/email"};
        } else {
            openPaths = new String[]{"/authorize", "/token", "/register", "/indoctrinate", "/email", "/user"};
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers(openPaths).permitAll()
            .and().csrf().disable();
        // ... more configuration, e.g. for form login
        http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(new ZevrantClientDetailsService(userProvider))
            .passwordEncoder(passwordEncoder);
        super.configure(http);
    }

}
