package net.zevrant.services.zevrant.oauth2.service.rest.response;

public class RegistrationResponse {

    private String userId;
    private boolean isSuccessful;

    public RegistrationResponse(String userId, boolean isSuccessful) {
        this.userId = userId;
        this.isSuccessful = isSuccessful;
    }

    public String getUserId() {
        return userId;
    }

    public boolean getIsSuccessful() {
        return isSuccessful;
    }
}
