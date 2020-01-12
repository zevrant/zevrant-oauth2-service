package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
@SequenceGenerator(name="users", initialValue=1, allocationSize=1000)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersIdSeq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long registrationId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

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
}
