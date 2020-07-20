package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.Registration;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserAlreadyExistsException;
import net.zevrant.services.zevrant.oauth2.service.repository.RegistrationRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import net.zevrant.services.zevrant.oauth2.service.rest.response.RegistrationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    public boolean register(String username, String password) throws UserAlreadyExistsException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("The given username has already been taken, please choose a different one and try again");
        }
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setUsername(username);
        newUser = userRepository.save(newUser);

        return true;
    }

    public RegistrationCode indoctrinate() {
        UUID uuid = UUID.randomUUID();
        Registration registration = new Registration(uuid.toString(), LocalDateTime.now().plusDays(1L));
        Registration code = registrationRepository.save(registration);
        return new RegistrationCode(code.getRegistrationCode(), code.getExpirationDate());
    }

    public void sendEmail(String clientId, String fullName, List<String> requestedRoles) {
        // Recipient's email ID needs to be mentioned.
        String to = "gerethd@gmail.com";

        // Sender's email ID needs to be mentioned
        String from = "gerethd@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("workmailtome@gmail.com", "brbnyganfhfnfktp");

            }

        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Indoctrination request for " + fullName);

            String messageText = "Username: ".concat(clientId).concat("\nRequested Roles: ");
            for (int i = 0; i < requestedRoles.size(); i++) {
                messageText.concat(requestedRoles.get(i).concat(","));
            }
            // Now set the actual message
            message.setText(messageText.substring(0, messageText.length() - 2));

            logger.debug("sending...");
            // Send message
            Transport.send(message);
            logger.debug("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
