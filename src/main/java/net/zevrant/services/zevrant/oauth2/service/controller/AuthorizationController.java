package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.response.AuthorizationResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/authorize")
public class AuthorizationController {

    private TokenService tokenService;

    @Autowired
    public AuthorizationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping
    public AuthorizationResponse getAuthorization(@RequestHeader("Authorization") String authorization, @RequestParam String username) {
        String token = null;
        try {
            token = authorization.split(" ")[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return new AuthorizationResponse(false, null);
        }
        LocalDateTime expirationDate = tokenService.isAuthorized(token, username);
        AuthorizationResponse response = new AuthorizationResponse();
        response.setExpires(expirationDate);
        response.setAuthorized(expirationDate != null);
        return response;
    }
}
