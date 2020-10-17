package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails;
import net.zevrant.services.zevrant.oauth2.service.entity.OAuth2Request;
import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.IncorrectPasswordException;
import net.zevrant.services.zevrant.oauth2.service.exceptions.InvalidOTPException;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserIsDisabledException;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.bouncycastle.cms.CMSException;
import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final DefaultTokenServices tokenServices;
    private final JwtAccessTokenConverter accessTokenConverter;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final UserService userService;

    @Autowired
    public TokenService(TokenRepository tokenRepository, DefaultTokenServices defaultTokenServices, UserRepository userRepository,
                        JwtAccessTokenConverter accessTokenConverter, PasswordEncoder passwordEncoder, EncryptionService encryptionService,
                        UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.tokenServices = defaultTokenServices;
        this.userRepository = userRepository;
        this.accessTokenConverter = accessTokenConverter;
        this.encryptionService = encryptionService;
        this.userService = userService;
    }

    @Transactional
    public OAuth2AccessToken getAccessToken(User user) {
        Optional <Token> tokenProxy = tokenRepository.findByClientId(user.getUsername());
        tokenProxy.ifPresent(token -> tokenRepository.deleteTokenByClientId(token.getClientId()));
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
            logger.debug("USER {} NOT FOUND", clientId);
            throw new UserNotFoundException("User " + clientId + " not found");
        }
        User details = detailsProxy.get();
        if (details.isDisabled() == Boolean.TRUE) {
            throw new UserIsDisabledException("User " + details.getUsername() + " is disabled, finish the password reset process to continue");
        }
        OAuth2Authentication authentication = new OAuth2Authentication(request, new ClientDetails(details.getUsername(), details.getPassword()));
        if (!passwordEncoder.matches(clientSecret, details.getPassword())) {
            logger.debug("USER {} provided incorrect password", clientId);
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

    public String getUsernameByToken(String token) {
        List<Token> tokenList = tokenRepository.findAllByToken(token);
        return tokenList.get(0).getClientId();
    }

    @Transactional
    public boolean logout(String username) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals(username)) {
            byte[] token = tokenRepository.findByClientId(username).get().getToken();
            tokenRepository.deleteTokenByClientId(username);
            return true;
        }
        return false;
    }
}


