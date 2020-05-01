package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.PasswordMismatchException;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.rest.request.UserUpdate;
import net.zevrant.services.zevrant.oauth2.service.rest.response.UserResponse;
import net.zevrant.services.zevrant.oauth2.service.rest.response.UsernameResponse;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import net.zevrant.services.zevrant.oauth2.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private TokenService tokenService;
    private UserService userService;

    @Autowired
    public UserController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @GetMapping("/username")
    public UsernameResponse getUsername(@RequestHeader String authorization) {

        return new UsernameResponse(tokenService.getUsername(authorization.split(" ")[1]));
    }

    @GetMapping("/{username}")
    public UserResponse getUser(@PathVariable String username) {
        User user = userService.getUser(username);
        return new UserResponse(username, user.getEmailAddress(), userService.convertRoles(user.getRoles()));
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody @Valid UserUpdate userUpdate) {
        if (!userUpdate.getPassword().equals(userUpdate.getPasswordConfirmation())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        User user = userService.updateUser(userUpdate.getOriginalUsername(), userUpdate.getUsername(), userUpdate.getPassword(),
                userUpdate.getEmailAddress(), userUpdate.getRoles(), userUpdate.isSubscribed());
        return new UserResponse(user.getUsername(), user.getEmailAddress(), userService.convertRoles(user.getRoles()));
    }
}
