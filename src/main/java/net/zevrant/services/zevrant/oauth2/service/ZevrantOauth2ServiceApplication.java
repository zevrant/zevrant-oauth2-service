package net.zevrant.services.zevrant.oauth2.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@EnableEurekaClient
@SpringBootApplication
public class ZevrantOauth2ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZevrantOauth2ServiceApplication.class, args);
	}


}
