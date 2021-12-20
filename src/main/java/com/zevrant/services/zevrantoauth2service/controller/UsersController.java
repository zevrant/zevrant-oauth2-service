package com.zevrant.services.zevrantoauth2service.controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.zevrant.services.zevrantoauth2service.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {


    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/me/username")
    public Mono<String> getMyUsername() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> ((Jwt) securityContext.getAuthentication().getPrincipal())
                        .getClaim("preferred_username"));
    }

    @GetMapping("/me/roles")
    public Flux<Object> getRoles() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMapIterable(securityContext -> {
                    JSONObject realmAccess = (JSONObject) ((Jwt) securityContext.getAuthentication().getPrincipal())
                            .getClaims()
                            .get("realm_access");
                    return (List<String>) realmAccess.get("roles");
                });
    }
}
