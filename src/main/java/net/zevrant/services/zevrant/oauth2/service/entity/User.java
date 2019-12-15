package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "USER")
@SequenceGenerator(name="users", initialValue=1, allocationSize=1000)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users")
    @Column(name = "ID", updatable = false, nullable = false)
    private Long ID;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    public User() {
    }

    public Long getID() {
        return ID;
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
