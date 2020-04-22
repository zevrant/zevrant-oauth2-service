package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Role> getRolesByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("User " + username + " not found");
        });
        return user.getRoles();
    }

    public List<String> convertRoles(List<Role> roles) {
        List<String> newRoles = new ArrayList<>();
        roles.forEach((role) -> newRoles.add(role.getRoleName()));
        return newRoles;
    }
}
