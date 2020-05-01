package net.zevrant.services.zevrant.oauth2.service.repository;

import net.zevrant.services.zevrant.oauth2.service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
