package net.zevrant.services.zevrant.oauth2.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantAuthentication;
import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantPrincipal;
import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.rest.request.LoginRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.response.TokenResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import net.zevrant.services.zevrant.oauth2.service.service.UserService;
import org.bouncycastle.cms.CMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/token")
public class AuthorizationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public AuthorizationController(TokenService tokenService, UserService userService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping
    public TokenResponse login(@RequestHeader String client_id, @RequestHeader String client_secret,
                               @RequestHeader Optional<String> oneTimePad) throws CMSException {
        try {
            OAuth2AccessToken oAuth2AccessToken = tokenService.getAccessToken(client_id, client_secret, oneTimePad);
            TokenResponse response = new TokenResponse();
            response.setAccessToken(oAuth2AccessToken.getValue());
            response.setExpiresIn(oAuth2AccessToken.getExpiresIn());
            response.setScope(oAuth2AccessToken.getScope());
            response.setTokenType(oAuth2AccessToken.getTokenType());
            return response;
        } catch (ClientRegistrationException ex) {
            throw new UserNotFoundException("User " + client_id + " not found");
        }
    }

    @PostMapping("/request_body")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) throws CMSException {
        return login(loginRequest.getClient_id(), loginRequest.getClientSecret(), Optional.of(loginRequest.getOneTimePad()));
    }

    @GetMapping
    public Authentication getAuthorization(@RequestHeader String authorization) throws JsonProcessingException {
        String token = authorization.split(" ")[1];
        String username = tokenService.getUsername(token);
        logger.debug("USERNAME: {}", username);
        List<Role> roles = userService.getRolesByUsername(username);
        logger.debug("Found roles: {}", roles);
        ZevrantAuthentication authentication = userService.getAuthentication(username);
        authentication.setAuthorities(userService.convertRoles(roles));
        authentication.setCredentials(authorization.split(" ")[1]);
        authentication.setPrincipal(new ZevrantPrincipal(username));

        return authentication;
    }

    @DeleteMapping
    public boolean logout(@RequestHeader String authorization) {
        return tokenService.logout(authorization.split(" ")[1]);
    }
}
