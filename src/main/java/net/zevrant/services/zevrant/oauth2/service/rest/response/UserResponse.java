package net.zevrant.services.zevrant.oauth2.service.rest.response;

import net.zevrant.services.security.common.secrets.management.rest.response.ZevrantGrantedAuthority;

import java.util.List;

public class UserResponse {

    private String username;
    private String emailAddress;
    private List<ZevrantGrantedAuthority> roles;
    private boolean subscribed;
    private boolean twoFactorEnabled;
    private String twoFactorSecret;

    public UserResponse(String username, String emailAddress, List<ZevrantGrantedAuthority> roles, boolean subscribed, boolean twoFactorEnabled) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.roles = roles;
        this.subscribed = subscribed;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<ZevrantGrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(List<ZevrantGrantedAuthority> roles) {
        this.roles = roles;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
