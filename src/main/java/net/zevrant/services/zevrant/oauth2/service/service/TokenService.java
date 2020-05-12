package net.zevrant.services.zevrant.oauth2.service.service;

import com.amazonaws.util.Base64;
import net.zevrant.services.zevrant.oauth2.service.config.AuthenticationManager;
import net.zevrant.services.zevrant.oauth2.service.config.SecretResource;
import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.InvalidOTPException;
import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.UserIsDisabledException;
import net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails;
import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.IncorrectPasswordException;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.bouncycastle.cms.CMSException;
import org.jboss.aerogear.security.otp.Totp;
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
    private EncryptionService encryptionService;

    @Autowired
    public TokenService(TokenRepository tokenRepository, DefaultTokenServices defaultTokenServices, UserRepository userRepository,
                        JwtAccessTokenConverter accessTokenConverter, AuthenticationManager authenticationManager,
                        @Value("${zevrant.ssl.key-store}") File keystore,
                        @Value("${zevrant.ssl.key-store-password}") String keystorePassword,
                        @Value("${oauth2.keystore.alias}") String keystoreAlias,
                        PasswordEncoder passwordEncoder, EncryptionService encryptionService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.tokenServices = defaultTokenServices;
        this.userRepository = userRepository;
        this.accessTokenConverter = accessTokenConverter;
        this.authenticationManager = authenticationManager;
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new SecretResource(keystore), keystorePassword.toCharArray());
        secret = Base64.encodeAsString(keyStoreKeyFactory.getKeyPair(keystoreAlias).getPrivate().getEncoded());
        this.encryptionService = encryptionService;
    }

    @Transactional
    public OAuth2AccessToken getAccessToken(String clientId, String clientSecret, Optional<String> oneTimePad) throws CMSException {
        Optional<OAuth2Authentication> authenticationProxy = authenticate(clientId, clientSecret, oneTimePad);
        if (authenticationProxy.isEmpty()) {
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

    @Transactional
    public OAuth2AccessToken getAccessToken(User user) {
        OAuth2Request request = new OAuth2Request(user.getUsername());
        OAuth2Authentication authentication = new OAuth2Authentication(request, new ClientDetails(user.getUsername(), user.getPassword()));
        OAuth2AccessToken accessToken = tokenServices.createAccessToken(authentication);
        accessToken = accessTokenConverter.enhance(accessToken, authentication);
        user.setDisabled(true);
        userRepository.save(user);
        return accessToken;
    }

    private Optional<OAuth2Authentication> authenticate(String clientId, String clientSecret, Optional<String> oneTimePad) throws CMSException {
        OAuth2Request request = new OAuth2Request(clientId);
        Optional<User> detailsProxy = userRepository.findByUsername(clientId);
        if (detailsProxy.isEmpty()) {
            throw new UserNotFoundException("User " + clientId + " not found");
        }
        User details = detailsProxy.get();
        if (details.isDisabled() == Boolean.TRUE) {
            throw new UserIsDisabledException("User " + details.getUsername() + " is disabled, finish the password reset process to continue");
        }
        OAuth2Authentication authentication = new OAuth2Authentication(request, new ClientDetails(details.getUsername(), details.getPassword()));
        if (!passwordEncoder.matches(clientSecret, details.getPassword())) {
            throw new IncorrectPasswordException("Password for " + clientId + " does not match");
        }
        if (oneTimePad.isEmpty() && details.isTwoFactorEnabeld()) {
            throw new InvalidOTPException("The 2FA code provided is invalid");
        }
        if (oneTimePad.isPresent()) {
            Totp totp = new Totp(new String(encryptionService.decryptData(details.getSecret())));
            if (!totp.verify(oneTimePad.get())) {
                throw new InvalidOTPException("The 2FA code provided is invalid");
            }
            return Optional.of(authentication);
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
