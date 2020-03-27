package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.rest.request.LoginRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.response.AuthorizationResponse;
import net.zevrant.services.zevrant.oauth2.service.rest.response.TokenResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class AuthorizationController {

    private TokenService tokenService;

    @Autowired
    public AuthorizationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public TokenResponse login(LoginRequest loginRequest) {
        try {
            OAuth2AccessToken oAuth2AccessToken = tokenService.GetAccessToken(loginRequest.getClient_id(), loginRequest.getClientSecret());
            TokenResponse response = new TokenResponse();
            response.setAccessToken(oAuth2AccessToken.getValue());
            response.setExpiresIn(oAuth2AccessToken.getExpiresIn());
            response.setScope(oAuth2AccessToken.getScope());
            response.setTokenType(oAuth2AccessToken.getTokenType());
            return response;
        } catch (ClientRegistrationException ex) {
            throw new UserNotFoundException("User " + loginRequest.getClient_id() + " not found");
        }
    }

    @GetMapping
    public AuthorizationResponse getAuthorization() {
        SecurityContext context = SecurityContextHolder.getContext();
        AuthorizationResponse response =  new AuthorizationResponse();
        response.setAuthorized(context.getAuthentication().isAuthenticated());
        return response;
    }
}
