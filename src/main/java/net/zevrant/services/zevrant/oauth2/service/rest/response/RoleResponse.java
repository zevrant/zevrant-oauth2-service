package net.zevrant.services.zevrant.oauth2.service.rest.response;

import java.util.List;

public class RoleResponse {

    long totalRoles;
    List<String> roles;

    public RoleResponse(long totalRoles, List<String> roles) {
        this.totalRoles = totalRoles;
        this.roles = roles;
    }

    public long getTotalRoles() {
        return totalRoles;
    }

    public void setTotalRoles(long totalRoles) {
        this.totalRoles = totalRoles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
