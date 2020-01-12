package net.zevrant.services.zevrant.oauth2.service.repository;

import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    Token findByTokenAndUsername(String token, String username);
}
