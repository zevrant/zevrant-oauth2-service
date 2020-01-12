package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.Registration;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.RegistrationRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import net.zevrant.services.zevrant.oauth2.service.rest.response.RegistrationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    public boolean register(String username, String password, String registrationCode) {
        verifyRegistration(registrationCode);
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setUsername(username);
        newUser = userRepository.save(newUser);

        return true;
    }

    private void verifyRegistration(String registrationCode) {
        Registration registration = null;
        try {
            registration = registrationRepository.getOne(registrationCode);

            if(LocalDateTime.now().isAfter(registration.getExpirationDate())) {
                registrationRepository.delete(registration);
                throw new ResourceAccessException("Resource is expired removing from database");
            }
        } catch (EntityNotFoundException ex) {
            throw new ResourceAccessException("Could not find registration code " + registrationCode);
        }
        registrationRepository.delete(registration);
    }

    public RegistrationCode indoctrinate() {
        UUID uuid = UUID.randomUUID();
        Registration registration = new Registration(uuid.toString(), LocalDateTime.now().plusDays(1L));
        Registration code = registrationRepository.save(registration);
        return new RegistrationCode(code.getRegistrationCode(), code.getExpirationDate());
    }
}
