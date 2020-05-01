package net.zevrant.services.zevrant.oauth2.service.rest.response;

import java.util.List;

public class UserResponse {

    private String username;
    private String emailAddress;
    private List<String> roles;

    public UserResponse(String username, String emailAddress, List<String> roles) {
        this.username = username;
        this.emailAddress = emailAddress;
        this.roles = roles;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
