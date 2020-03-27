package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.response.UsernameResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private TokenService tokenService;

    @Autowired
    public UserController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/token")
    public UsernameResponse getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return new UsernameResponse(authentication.getName());
    }
}
