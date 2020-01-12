package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {

    private TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public LocalDateTime isAuthorized(String token, String username) {
        try{
            return tokenRepository.findByTokenAndUsername(token, username).getExpirationDate();
        } catch(NullPointerException ex) {
            return null;
        }
    }

}
