package net.zevrant.services.zevrant.oauth2.service.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "roleName")
    private String roleName;

    @Column(name = "roleDesc")
    private String roleDescription;

    @Column(name = "role_type")
    private String roleType;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "roleAssociation",
            inverseJoinColumns = {
                    @JoinColumn(name = "username", referencedColumnName = "username",
                            nullable = false, updatable = false)},
            joinColumns = {
                    @JoinColumn(name = "role", referencedColumnName = "roleName",
                            nullable = false, updatable = false)})
    private List<User> users;

    public Role() {
    }


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
}
