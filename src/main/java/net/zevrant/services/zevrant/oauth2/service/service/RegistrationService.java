package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public RegistrationService() {
    }

    public boolean register(String username, String password) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setUsername(username);
        newUser = userRepository.save(newUser);
        return newUser != null;
    }
}
