package net.zevrant.services.zevrant.oauth2.service.rest.response;

import java.time.LocalDateTime;

public class RegistrationCode {

    private String registrationCode;
    private LocalDateTime expirationDate;

    public RegistrationCode(String registrationCode, LocalDateTime expirationDate) {
        this.registrationCode = registrationCode;
        this.expirationDate = expirationDate;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
}
