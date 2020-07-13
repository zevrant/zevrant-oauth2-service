package net.zevrant.services.zevrant.oauth2.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ZevrantOauth2ServiceApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Security.setProperty("crypto.policy", "unlimited");
        int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Max Key Size for AES : " + maxKeySize);
        SpringApplication.run(ZevrantOauth2ServiceApplication.class, args);
    }


}
