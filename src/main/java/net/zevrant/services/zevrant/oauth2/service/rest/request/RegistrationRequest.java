package net.zevrant.services.zevrant.oauth2.service.rest.request;

import java.util.List;

public class RegistrationRequest {

    private String clientId;
    private String clientSecret;
    private List<String> roles;
    private String fullName;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String clientId, String clientSecret, List<String> roles, String fullName) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.roles = roles;
        this.fullName = fullName;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
