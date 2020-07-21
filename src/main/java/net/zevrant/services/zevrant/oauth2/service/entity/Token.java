package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_access_token")
public class Token {

    @Id
    @Column(name = "token", nullable = false, unique = true)
    private byte[] token;

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "refresh_token")
    private String refreshToken;

    public Token() {
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
