package net.zevrant.services.zevrant.oauth2.service.users;


import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Service
public class UserProvider implements ClientDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserProvider.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserProvider(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        User user = userRepository.findByUsername(clientId);
        if(user == null) {
            logger.error("Username {} not found", clientId);
            throw new ClientRegistrationException("Username not found");
        }
        return new ZevrantsClientDetails(user.getUsername(), user.getPassword());
    }

    public ClientDetails locadClientByToken(String token) {
        Optional<Token> tokenDb = tokenRepository.findByToken(token);
        if(tokenDb.isEmpty()) {
            return null;
        }
        return loadClientByClientId(tokenDb.get().getUsername());
    }
}
