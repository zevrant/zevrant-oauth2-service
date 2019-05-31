package net.zevrant.services.zevrant.oauth2.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter implements WebMvcConfigurer {

    private final AuthenticationManager authenticationManager;

    private final ClientDetailsService userProvider;

    private final String keystorePassword;

    private final String keystoreAlias;

    private final SecretResource keystore;

    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager, ClientDetailsService userProvider,
                                     @Value("${encrypted.properties.oauth2.oauth2.keystore.password}") String keystorePassword,
                                     @Value("${encrypted.properties.oauth2.oauth2.keystore.store}") File keystore,
                                     @Value("${encrypted.properties.oauth2.oauth2.keystore.alias}") String keystoreAlias) {
        this.authenticationManager = authenticationManager;
        this.userProvider = userProvider;
        this.keystorePassword = keystorePassword;
        this.keystore = new SecretResource(keystore);
        this.keystoreAlias = keystoreAlias;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(userProvider).inMemory().build();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(new TokenEnhancerChain())
                .tokenStore(tokenStore());

    }

    @Primary
    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("sha256", new StandardPasswordEncoder());
        encoders.put("null", encoders.get("noop"));

        return new DelegatingPasswordEncoder("sha256", encoders);

    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(keystore, keystorePassword.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(keystoreAlias));
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }



}
