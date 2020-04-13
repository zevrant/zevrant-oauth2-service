package net.zevrant.services.zevrant.oauth2.service.service;

import com.amazonaws.util.Base64;
import net.zevrant.services.zevrant.oauth2.service.config.AuthenticationManager;
import net.zevrant.services.zevrant.oauth2.service.config.SecretResource;
import net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails;
import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.IncorrectPasswordException;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDateTime;
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

    @Transactional
    public OAuth2AccessToken GetAccessToken(String clientId,  String clientSecret) {
        Optional<OAuth2Authentication> authenticationProxy = authenticate(clientId, clientSecret);
        if(authenticationProxy.isEmpty()){
            return null;
        }
        OAuth2Authentication authentication = authenticationProxy.get();
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

    private Optional<OAuth2Authentication> authenticate(String clientId, String clientSecret) {
        OAuth2Request request = new OAuth2Request(clientId);
        Optional<User> detailsProxy = userRepository.findByUsername(clientId);
        if(detailsProxy.isEmpty()) {
            throw new UserNotFoundException("User " + clientId + " not found");
        }
        User details = detailsProxy.get();
        OAuth2Authentication authentication = new OAuth2Authentication(request, new ClientDetails(details.getUsername(), details.getPassword()));
        if(!passwordEncoder.matches(clientSecret, details.getPassword())){
            throw new IncorrectPasswordException("Password for " + clientId + " does not match");
        }
        return Optional.of(authentication);
    }

    public LocalDateTime isAuthorized(String token) {
        Optional<Token> tokenDb = tokenRepository.findByToken(token);
        return tokenDb.map(Token::getExpirationDate).orElse(null);
    }

    public String getUsername(String token) {
        Optional<Token> tokenProxy =  tokenRepository.findByToken(token);
        if(tokenProxy.isEmpty()){ throw new UserNotFoundException("Cannot find user for token"); }
        return tokenProxy.get().getUsername();
    }

    @Transactional
    public boolean logout(String token) {
        return tokenRepository.deleteTokenByToken(token) > 0;
    }
}
