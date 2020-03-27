package net.zevrant.services.zevrant.oauth2.service.service;

import com.amazonaws.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.zevrant.services.zevrant.oauth2.service.config.AuthenticationManager;
import net.zevrant.services.zevrant.oauth2.service.config.SecretResource;
import net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails;
import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import net.zevrant.services.zevrant.oauth2.service.users.UserProvider;
import net.zevrant.services.zevrant.oauth2.service.users.ZevrantsClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class TokenService {

    private TokenRepository tokenRepository;
    private UserRepository userRepository;
    private DefaultTokenServices tokenServices;
    private JwtAccessTokenConverter accessTokenConverter;
    private AuthenticationManager authenticationManager;
    private String secret;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public TokenService(TokenRepository tokenRepository, DefaultTokenServices defaultTokenServices, UserRepository userRepository,
                        JwtAccessTokenConverter accessTokenConverter, AuthenticationManager authenticationManager,
                        @Value("${zevrant.ssl.key-store}") File keystore,
                        @Value("${zevrant.ssl.key-store-password}") String keystorePassword,
                        @Value("${oauth2.keystore.alias}") String keystoreAlias,
                        PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.tokenServices = defaultTokenServices;
        this.userRepository = userRepository;
        this.accessTokenConverter = accessTokenConverter;
        this.authenticationManager = authenticationManager;
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new SecretResource(keystore), keystorePassword.toCharArray());
        secret = Base64.encodeAsString(keyStoreKeyFactory.getKeyPair(keystoreAlias).getPrivate().getEncoded());
    }

    public OAuth2AccessToken GetAccessToken(String clientId,  String clientSecret) {
        String token = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(clientId)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        OAuth2Request request = new OAuth2Request(token);
        User details = userRepository.findByUsername(clientId);
        OAuth2Authentication authentication = new OAuth2Authentication(request, new ClientDetails(details.getUsername(), details.getPassword()));
        String encryptedSecret = passwordEncoder.encode(clientSecret);
        if(!encryptedSecret.equals(details.getPassword())){
            return null;
        }
        authentication.setAuthenticated(true);
        OAuth2AccessToken accessToken = tokenServices.createAccessToken(authentication);
        accessToken = accessTokenConverter.enhance(accessToken, authentication);
        Token dbToken = new Token();
        dbToken.setToken(accessToken.getValue());
        dbToken.setUsername(clientId);
        dbToken.setExpirationDate(LocalDateTime.now().plusSeconds(accessToken.getExpiresIn() / 1000));
        tokenRepository.save(dbToken);
        return accessToken;
    }

    public LocalDateTime isAuthorized(String token) {
        Optional<Token> tokenDb = tokenRepository.findByToken(token);
        return tokenDb.map(Token::getExpirationDate).orElse(null);
    }


}
