package net.zevrant.services.zevrant.oauth2.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ZevrantOauth2ServiceApplication {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Security.setProperty("crypto.policy", "unlimited");
        int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        System.out.println("Max Key Size for AES : " + maxKeySize);
        ConfigurableApplicationContext ctx = SpringApplication.run(ZevrantOauth2ServiceApplication.class, args);
        DispatcherServlet dispatcherServlet = (DispatcherServlet) ctx.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
    }


}
