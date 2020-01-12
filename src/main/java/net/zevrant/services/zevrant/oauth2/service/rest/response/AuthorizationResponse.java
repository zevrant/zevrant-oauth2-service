package net.zevrant.services.zevrant.oauth2.service.rest.response;

import java.time.LocalDateTime;

public class AuthorizationResponse {

    private boolean isAuthorized;
    private LocalDateTime expires;

    public AuthorizationResponse() {
    }

    public AuthorizationResponse(boolean isAuthorized, LocalDateTime expires) {
        this.isAuthorized = isAuthorized;
        this.expires = expires;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
}
