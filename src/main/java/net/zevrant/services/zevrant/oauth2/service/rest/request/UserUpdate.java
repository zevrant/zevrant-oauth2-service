package net.zevrant.services.zevrant.oauth2.service.rest.request;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserUpdate {

    @NotNull
    private String originalUsername;
    private String username;
    private String password;
    private String passwordConfirmation;
    @NotNull
    private String emailAddress;
    @NotNull
    private boolean subscribed;
    private List<String> roles;
    private boolean twoFactorEnabled;

    public UserUpdate() {
    }

    public UserUpdate(String username, @NotNull String originalUsername, String password, String passwordConfirmation,
                      @NotNull String emailAddress, boolean subscribed, List<String> roles, boolean twoFactorEnabled) {
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.emailAddress = emailAddress;
        this.subscribed = subscribed;
        this.roles = roles;
        this.originalUsername = originalUsername;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getOriginalUsername() {
        return originalUsername;
    }

    public void setOriginalUsername(String originalUsername) {
        this.originalUsername = originalUsername;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
}
