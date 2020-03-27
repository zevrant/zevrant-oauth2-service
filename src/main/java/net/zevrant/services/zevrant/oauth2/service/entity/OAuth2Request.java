package net.zevrant.services.zevrant.oauth2.service.entity;

public class OAuth2Request extends org.springframework.security.oauth2.provider.OAuth2Request {

    public OAuth2Request(String clientId) {
        super(clientId);
    }
}
