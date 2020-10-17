package net.zevrant.services.zevrant.oauth2.service.repository;

import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, byte[]> {

    Optional<Token> findByTokenAndClientId(byte[] token, String username);

    Optional<Token> findTokenByToken(String token);

    Optional<Token> findByClientId(String clientId);

    int deleteTokenByClientId(String token);
}
