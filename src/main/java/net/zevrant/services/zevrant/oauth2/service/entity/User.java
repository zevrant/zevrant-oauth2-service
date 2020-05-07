package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@SequenceGenerator(name = "users", initialValue = 1, allocationSize = 1000)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersIdSeq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long registrationId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "subscribed")
    private boolean subscribed;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabeld;

    @Column(name = "two_factor_secret")
    private String secret;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "roleAssociation",
            joinColumns = {
                    @JoinColumn(name = "username", referencedColumnName = "username",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "role", referencedColumnName = "rolename",
                            nullable = false, updatable = false)})
    private List<Role> roles;

    public User() {
    }

    public Long getRegistrationId() {
        return registrationId;
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

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
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

    public boolean isTwoFactorEnabeld() {
        return twoFactorEnabeld;
    }

    public void setTwoFactorEnabeld(boolean twoFactorEnabeld) {
        this.twoFactorEnabeld = twoFactorEnabeld;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
