package net.zevrant.services.zevrant.oauth2.service.rest.response;

public class Email {
    private boolean successful;

    public Email(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
