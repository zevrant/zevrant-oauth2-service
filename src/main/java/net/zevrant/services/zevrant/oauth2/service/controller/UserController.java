package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.PasswordMismatchException;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import net.zevrant.services.zevrant.oauth2.service.rest.request.AddRole;
import net.zevrant.services.zevrant.oauth2.service.rest.request.ForgotPasswordRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.request.UserUpdate;
import net.zevrant.services.zevrant.oauth2.service.rest.response.RoleResponse;
import net.zevrant.services.zevrant.oauth2.service.rest.response.UserResponse;
import net.zevrant.services.zevrant.oauth2.service.rest.response.UsernameResponse;
import net.zevrant.services.zevrant.oauth2.service.service.EncryptionService;
import net.zevrant.services.zevrant.oauth2.service.service.TokenService;
import net.zevrant.services.zevrant.oauth2.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserService userService;
    private final EncryptionService encryptionService;

    @Autowired
    public UserController(TokenService tokenService, UserService userService, UserRepository userRepository,
                          EncryptionService encryptionService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/username")
    public UsernameResponse getUsername(@RequestHeader("Authorization") String authorization) {
        String token = tokenService.getUsernameByToken(authorization.split(" ")[1]);
        return new UsernameResponse(token);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyAuthority('admin', 'dnd-service')")
    public UserResponse getUser(@PathVariable String username) {
        User user = userService.getUser(username);
        return new UserResponse(username, user.getEmailAddress(), userService.convertRoleStrings(user.getRoles()),
                user.isSubscribed(), user.isTwoFactorEnabeld());
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('admin', 'dnd-service')")
    public UserResponse updateUser(@RequestBody @Valid UserUpdate userUpdate) throws CertificateEncodingException, IOException, CMSException {
        boolean twoFactorEnabled = userService.getUser(userUpdate.getOriginalUsername()).isTwoFactorEnabeld();
        if (StringUtils.isNotBlank(userUpdate.getPassword())
                && !userUpdate.getPassword().equals(userUpdate.getPasswordConfirmation())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        User user = userService.updateUser(userUpdate.getOriginalUsername(), userUpdate.getUsername(), userUpdate.getPassword(),
                userUpdate.getEmailAddress(), userUpdate.getRoles(), userUpdate.isSubscribed(), userUpdate.isTwoFactorEnabled());
        UserResponse response = new UserResponse(user.getUsername(), user.getEmailAddress(),
                userService.convertRoleStrings(user.getRoles()), user.isSubscribed(), user.isTwoFactorEnabeld());
        if (!twoFactorEnabled && userUpdate.isTwoFactorEnabled()) {
            response.setTwoFactorSecret(new String(encryptionService.decryptData(user.getSecret())));
        }
        return response;
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        User user = userService.getUserByEmail(request.getEmailAddress());
        OAuth2AccessToken token = tokenService.getAccessToken(user);
        String from = "no-reply@zevrant-services.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("workmailtome@gmail.com", "brbnyganfhfnfktp");
            }

        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmailAddress()));
            message.setSubject("Password Reset For ".concat(user.getUsername()));
            message.setText("password reset can be initiated at https://zevrant-services.com:7644/zevrant-home-ui/password-reset/" + token.getValue());
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    @PutMapping("/password-reset")
    public void resetPassword(@RequestBody UserUpdate request) throws CertificateEncodingException, IOException, CMSException {
        userService.updateUser(request.getOriginalUsername(), request.getUsername(),
                request.getPassword(), request.getPasswordConfirmation(), request.getRoles(),
                request.isSubscribed(), request.isTwoFactorEnabled());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/all-roles")
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<String> getAllRoles() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("dnd-service")) {
            return userService.getAllRoles(Optional.of("D&D"));
        }
        return userService.getAllRoles(Optional.empty());
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('admin')")
    public boolean updatesUsers(@RequestBody List<UserResponse> users) throws IOException, CMSException, CertificateEncodingException {
        for (UserResponse user : users) {
            userService.updateUser(user.getUsername(), user.getUsername(), null, user.getEmailAddress(), user.getRoles(), user.isSubscribed(), user.isTwoFactorEnabled());
        }
        return true;
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAnyAuthority('admin')")
    public boolean addRole(@RequestBody AddRole role) {
        userService.addRole(role);
        return true;
    }

    @GetMapping("/roles/{username}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public List<String> getUserRoles(@PathVariable("username") String username) {
        List<Role> roles = userService.getUserRoles(username);
        List<String> roleStrings = new ArrayList<>();
        for (Role role : roles) {
            roleStrings.add(role.getRoleName());
        }
        return roleStrings;
    }

    @GetMapping("/roles/search/{page}/{pageSize}")
    @PreAuthorize("hasAnyAuthority('admin')")
    public RoleResponse getAllRoles(@PathVariable("page") int page, @PathVariable("pageSize") int pageSize) {
        return userService.searchRoles(page, pageSize);
    }


}
