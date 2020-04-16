package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.rest.request.LoginRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.response.TokenResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
public class AuthorizationController {

    private TokenService tokenService;

    @Autowired
    public AuthorizationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public TokenResponse login(@RequestHeader String client_id, @RequestHeader String client_secret) {
        try {
            OAuth2AccessToken oAuth2AccessToken = tokenService.GetAccessToken(client_id, client_secret);
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
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        return login(loginRequest.getClient_id(), loginRequest.getClientSecret());
    }

    @GetMapping
    public Authentication getAuthorization() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    @DeleteMapping
    public boolean logout(@RequestHeader String authorization) {
        return tokenService.logout(authorization.split(" ")[1]);
    }
}
