package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGISTER")
public class Registration {

    @Id
    @Column(name = "registrationCode")
    private String registrationCode;

    @Column(name = "expirationDate")
    private LocalDateTime expirationDate;

    public Registration() {
    }

    public Registration(String registrationCode, LocalDateTime expirationDate) {
        this.registrationCode = registrationCode;
        this.expirationDate = expirationDate;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }
}
