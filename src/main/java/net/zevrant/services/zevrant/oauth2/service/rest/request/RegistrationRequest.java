package net.zevrant.services.zevrant.oauth2.service.rest.request;

import java.util.List;

public class RegistrationRequest {

    private String clientId;
    private String clientSecret;
    private String registrationCode;
    private List<String> roles;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String clientId, String clientSecret, List<String> roles) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }
}
