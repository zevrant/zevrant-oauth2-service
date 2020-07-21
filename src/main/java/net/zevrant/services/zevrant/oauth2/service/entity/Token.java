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
    private String token;

    @Column(name = "client_id", nullable = false, unique = true)
    private byte[] clientId;

    @Column(name = "refresh_token")
    private String refreshToken;

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientId() {
        return new String(clientId);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId.getBytes();
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
