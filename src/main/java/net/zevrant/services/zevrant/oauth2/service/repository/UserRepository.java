package net.zevrant.services.zevrant.oauth2.service.repository;

import net.zevrant.services.zevrant.oauth2.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
