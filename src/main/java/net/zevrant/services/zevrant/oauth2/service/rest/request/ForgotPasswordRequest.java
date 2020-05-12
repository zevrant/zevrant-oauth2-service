package net.zevrant.services.zevrant.oauth2.service.rest.request;

public class ForgotPasswordRequest {

    private String emailAddress;

    public ForgotPasswordRequest() {
    }

    public ForgotPasswordRequest(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
