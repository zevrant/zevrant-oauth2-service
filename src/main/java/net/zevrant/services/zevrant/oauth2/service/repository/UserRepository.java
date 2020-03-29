package net.zevrant.services.zevrant.oauth2.service.repository;

import java.util.Optional;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, ClientRegistrationRepository {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndAndPassword(String username, String password);
}
