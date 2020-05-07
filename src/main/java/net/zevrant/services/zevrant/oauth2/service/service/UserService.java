package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.controller.exceptions.InvalidPasswordException;
import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.RoleRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.apache.commons.lang.StringUtils;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    public List<String> convertRoles(List<Role> roles) {
        List<String> newRoles = new ArrayList<>();
        roles.forEach((role) -> newRoles.add(role.getRoleName()));
        return newRoles;
    }

    public List<Role> convertStrings(List<String> roles) {
        List<Role> newRoles = new ArrayList<>();
        roles.forEach((role) -> {
            Optional<Role> roleProxy = roleRepository.findById(role);
            roleProxy.ifPresent(newRoles::add);

        });
        return newRoles;
    }

    public User updateUser(String originalUsername, String username, String password, String emailAddress, List<String> roles,
                           boolean subscribed, boolean twoFactorEnabled) {
        User user = getUser(originalUsername);
        manage2fa(user, twoFactorEnabled);
        user.setRoles(convertStrings(roles));
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
        userRepository.save(user);
        return user;
    }

    private void manage2fa(User user, boolean twoFactorEnabled) {
        if (user.isTwoFactorEnabeld() && twoFactorEnabled) {
            return;
        }
        if (!user.isTwoFactorEnabeld() && twoFactorEnabled) {
            user.setTwoFactorEnabeld(true);
            user.setSecret(Base32.random());
            return;
        }
        if (!twoFactorEnabled) {
            user.setTwoFactorEnabeld(false);
            user.setSecret(null);
        }
    }

}
