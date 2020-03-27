package net.zevrant.services.zevrant.oauth2.service.config;

import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.File;

@Configuration
public class TokenServicesConfig {

    private static final Logger logger = LoggerFactory.getLogger(TokenServicesConfig.class);

    private String keystoreAlias;
    private Resource keystore;
    private String keystorePassword;

    public TokenServicesConfig(@Value("${zevrant.ssl.key-store-password}") String keystorePassword,
                               @Value("${zevrant.ssl.key-store}") File keystore,
                               @Value("${oauth2.keystore.alias}") String keystoreAlias) {
        this.keystorePassword = keystorePassword;
        this.keystore = new SecretResource(keystore);
        this.keystoreAlias = keystoreAlias;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        TokenEnhancerChain enhancer = new TokenEnhancerChain();
        defaultTokenServices.setTokenEnhancer(enhancer);
//        defaultTokenServices.setAuthenticationManager(authenticationManager);

        return defaultTokenServices;
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
        logger.info("Set JWT signing key to: {}", converter.getKey());
        return converter;
    }
}
