package net.zevrant.services.zevrant.oauth2.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import net.zevrant.services.zevrant.oauth2.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Authentication getAuthorization() throws JsonProcessingException {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication();
    }

    @DeleteMapping("/{username}")
    public boolean logout(@PathVariable("username") String username) {
        return tokenService.logout(username);
    }
}
