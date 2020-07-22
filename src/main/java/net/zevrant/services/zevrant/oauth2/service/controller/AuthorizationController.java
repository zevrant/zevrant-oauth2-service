package net.zevrant.services.zevrant.oauth2.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantAuthentication;
import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantGrantedAuthority;
import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import net.zevrant.services.zevrant.oauth2.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

        ZevrantAuthentication auth = objectMapper.readValue(objectMapper.writeValueAsString(SecurityContextHolder.getContext().getAuthentication()), ZevrantAuthentication.class);
        List<Role> roles = userService.getRolesByUsername(auth.getName());
        List<ZevrantGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach((role) -> {
            authorities.add(new ZevrantGrantedAuthority(role.getRoleName()));
        });
        auth.setAuthorities(authorities);
        return auth;
    }

    @DeleteMapping("/{username}")
    public boolean logout(@PathVariable("username") String username) {
        return tokenService.logout(username);
    }
}
