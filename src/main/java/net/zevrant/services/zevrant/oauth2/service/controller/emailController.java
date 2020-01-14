package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.request.RegistrationConfig;
import net.zevrant.services.zevrant.oauth2.service.rest.response.Email;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@RestController
@RequestMapping("/email")
public class emailController {

  @PostMapping
  public Email sendEmail(@RequestBody RegistrationConfig config) throws MessagingException {
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
      message.setSubject("Indoctrination request for ".concat(config.getFullName()));

      // Now set the actual message
      message.setText("Username: ".concat(config.getUsername()).concat("\nPassword: ").concat(config.getPassword()));

      System.out.println("sending...");
      // Send message
      Transport.send(message);
      System.out.println("Sent message successfully....");
    } catch (MessagingException mex) {
      mex.printStackTrace();
    }
    return new Email(true);
  }
}
