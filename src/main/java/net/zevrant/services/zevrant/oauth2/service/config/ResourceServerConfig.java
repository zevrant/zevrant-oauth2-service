package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.security.common.secrets.management.filter.OAuthSecurityFilter;
import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantClientDetailsService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
@EnableWebSecurity
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    private final UserProvider userProvider;
    private final ConfigurableApplicationContext context;
    private final ZevrantOauthResponseClient responseClient;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final String[] openPaths;
    private final ClientDetailsService clientDetailsService;
    private final RestTemplate restTemplate;

    public ResourceServerConfig(UserProvider userProvider, ConfigurableApplicationContext context, ZevrantOauthResponseClient responseClient,
                                PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService,
                                RestTemplate restTemplate) {
        this.userProvider = userProvider;
        this.context = context;
        this.responseClient = responseClient;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.clientDetailsService = clientDetailsService;
        this.restTemplate = restTemplate;
        openPaths = new String[]{"/authorize", "/token", "/user/forgot-password", "/register", "/actuator/health", "/user/roles/search/*/*"};
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous().and()
                .authorizeRequests().antMatchers(openPaths).permitAll()
                .and().csrf().disable();
        // ... more configuration, e.g. for form login
        http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(new ZevrantClientDetailsService(userProvider))
                .passwordEncoder(passwordEncoder);
        http
                .addFilterBefore(new OAuthSecurityFilter("https://localhost:9001/", this.restTemplate, this.authenticationManager), AnonymousAuthenticationFilter.class);
        super.configure(http);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(openPaths);
    }

}
