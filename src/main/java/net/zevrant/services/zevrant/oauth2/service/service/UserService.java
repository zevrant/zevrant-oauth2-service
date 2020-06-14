package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantGrantedAuthority;
import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.InvalidPasswordException;
import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.RoleRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import net.zevrant.services.zevrant.oauth2.service.rest.request.AddRole;
import net.zevrant.services.zevrant.oauth2.service.rest.response.UserResponse;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private EncryptionService encryptionService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
    }

    public List<Role> getRolesByUsername(String username) {
        User user = getUser(username);
        return user.getRoles();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("User " + username + " not found");
        });
    }

    public List<ZevrantGrantedAuthority> convertRoles(List<Role> roles) {
        List<ZevrantGrantedAuthority> newRoles = new ArrayList<>();
        roles.forEach((role) -> newRoles.add(new ZevrantGrantedAuthority(role.getRoleName())));
        return newRoles;
    }

    public List<Role> convertStrings(List<String> roles) {
        List<Role> newRoles = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            roles.forEach((role) -> {
                Optional<Role> roleProxy = roleRepository.findById(role);
                roleProxy.ifPresent(newRoles::add);
            });
        }

        return newRoles;
    }

    public User updateUser(String originalUsername, String username, String password, String emailAddress, List<String> roles,
                           boolean subscribed, boolean twoFactorEnabled) throws CertificateEncodingException, IOException, CMSException {
        User user = getUser(originalUsername);
        manage2fa(user, twoFactorEnabled);
        if (roles != null) {
            user.setRoles(convertStrings(roles));
        }
        if (StringUtils.isNotBlank(username)) {
            user.setUsername(username);
        }
        if (StringUtils.isNotBlank(password)) {
            if (password.length() < 11) {
                throw new InvalidPasswordException("Password must be at least 11 characters long");
            }
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setEmailAddress(emailAddress);
        user.setSubscribed(subscribed);
        user.setDisabled(false);
        userRepository.save(user);
        return user;
    }

    private void manage2fa(User user, boolean twoFactorEnabled) throws CertificateEncodingException, IOException, CMSException {
        if (user.isTwoFactorEnabeld() && twoFactorEnabled) {
            return;
        }
        if (!user.isTwoFactorEnabeld() && twoFactorEnabled) {
            user.setTwoFactorEnabeld(true);
            user.setSecret(encryptionService.encryptData(Base32.random().getBytes()));
            return;
        }
        if (!twoFactorEnabled) {
            user.setTwoFactorEnabeld(false);
            user.setSecret(null);
        }
    }

    public User getUserByEmail(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress).orElseThrow(() -> {
            throw new UsernameNotFoundException("User " + emailAddress + " not found");
        });
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return convertUsers(users);
    }

    private List<UserResponse> convertUsers(Collection<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        users.forEach((user) -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setEmailAddress(user.getEmailAddress());
            userResponse.setRoles(convertRoleStrings(user.getRoles()));
            userResponse.setSubscribed(user.isSubscribed());
            userResponse.setUsername(user.getUsername());
            userResponses.add(userResponse);
        });
        return userResponses;
    }

    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        roleRepository.findAll().forEach((role -> roles.add(role.getRoleName())));
        return roles;
    }

    public List<String> convertRoleStrings(List<Role> roles) {
        List<String> roleStrings = new ArrayList<>();
        roles.forEach((role) -> {
            roleStrings.add(role.getRoleName());
        });
        return roleStrings;
    }

    public void addRole(AddRole role) {
        Role newRole = new Role();
        newRole.setRoleName(role.getRoleName());
        newRole.setRoleDescription(role.getRoleDesc());
        roleRepository.save(newRole);
    }

    public List<Role> getUserRoles(String username) {
        User user = getUser(username);
        return user.getRoles();
    }
}
