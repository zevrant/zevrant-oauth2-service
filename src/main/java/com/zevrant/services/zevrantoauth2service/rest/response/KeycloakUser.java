package com.zevrant.services.zevrantoauth2service.rest.response;

import java.util.List;
import java.util.UUID;

public class KeycloakUser {

    private UUID id;
    private long createdTimestamp;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private boolean totp;
    private boolean emailVerified;
    private List<Object> disableableCredentialTypes;
    private List<Object> requiredActions;
    private int notBefore;
    private KeycloackUserAccess access;

    public KeycloakUser() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTotp() {
        return totp;
    }

    public void setTotp(boolean totp) {
        this.totp = totp;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public List<Object> getDisableableCredentialTypes() {
        return disableableCredentialTypes;
    }

    public void setDisableableCredentialTypes(List<Object> disableableCredentialTypes) {
        this.disableableCredentialTypes = disableableCredentialTypes;
    }

    public List<Object> getRequiredActions() {
        return requiredActions;
    }

    public void setRequiredActions(List<Object> requiredActions) {
        this.requiredActions = requiredActions;
    }

    public int getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(int notBefore) {
        this.notBefore = notBefore;
    }

    public KeycloackUserAccess getAccess() {
        return access;
    }

    public void setAccess(KeycloackUserAccess access) {
        this.access = access;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
