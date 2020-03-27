package net.zevrant.services.zevrant.oauth2.service.rest.response;

public class UsernameResponse {

    private String username;

    public UsernameResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
