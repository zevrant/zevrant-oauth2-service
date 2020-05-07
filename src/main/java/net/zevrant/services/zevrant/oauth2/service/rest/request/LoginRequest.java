package net.zevrant.services.zevrant.oauth2.service.rest.request;

public class LoginRequest {

    private String grant_type;
    private String client_id;
    private String client_secret;
    private String scope;
    private String oneTimePad;

    public LoginRequest() {
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public void setClientSecret(String clientSecret) {
        this.client_secret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getOneTimePad() {
        return oneTimePad;
    }

    public void setOneTimePad(String oneTimePad) {
        this.oneTimePad = oneTimePad;
    }
}
