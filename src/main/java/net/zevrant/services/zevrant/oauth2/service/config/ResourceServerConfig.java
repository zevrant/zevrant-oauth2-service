package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantClientDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public ResourceServerConfig(UserProvider userProvider, ConfigurableApplicationContext context, ZevrantOauthResponseClient responseClient,
                                PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userProvider = userProvider;
        this.context = context;
        this.responseClient = responseClient;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        List<String> activeProfiles = Arrays.asList(context.getEnvironment().getActiveProfiles());
        if(activeProfiles.contains("prod")) {
            openPaths = new String[]{"/authorize", "/token", "/register", "/email"};
        } else {
            openPaths = new String[]{"/authorize", "/token", "/register", "/indoctrinate", "/email"};
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

//        http
//                .addFilterBefore(new OAuthJdbcFilter(userProvider, authenticationManager), AnonymousAuthenticationFilter.class);
        super.configure(http);
    }

//    @Override
//    public void configure(WebSecurity security) {
//        security.ignoring().mvcMatchers(openPaths);
//    }

    private static class OAuthJdbcFilter implements Filter {

        private static final Logger logger = LoggerFactory.getLogger(OAuthJdbcFilter.class);

        private UserProvider userProvider;
        private AuthenticationManager authenticationManager;

        public OAuthJdbcFilter(UserProvider userProvider, AuthenticationManager authenticationManager) {
            this.userProvider = userProvider;
            this.authenticationManager = authenticationManager;
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {

        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            ClientDetails details;
            try {
                details= userProvider.locadClientByToken(((HttpServletRequest) request).getHeader("Authorization"));
            } catch(ClientRegistrationException ex) {
                logger.warn("failed to find authorization for user token");
                chain.doFilter(request, response);
                return;
            }
            if((authentication == null || "anonymous".equals(authentication.getPrincipal())) && details != null) {
                OAuth2Request oauthRequest = new OAuth2Request(details.getClientId());
                OAuth2Authentication newAuthentication = new OAuth2Authentication(oauthRequest, new net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails(details.getClientId(), details.getClientSecret()));
                Authentication authenticated = authenticationManager.authenticate(newAuthentication);
                SecurityContextHolder.getContext().setAuthentication(authenticated);
                String principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            }
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {

        }
    }
}
