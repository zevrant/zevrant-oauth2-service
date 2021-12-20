package com.zevrant.services.zevrantoauth2service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zevrant.services"})
public class ZevrantOauth2ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZevrantOauth2ServiceApplication.class, args);
    }


}
