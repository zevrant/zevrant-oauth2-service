package net.zevrant.services.zevrant.oauth2.service.users;


import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

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
        Optional<User> userProxy = userRepository.findByUsername(clientId);
        if(userProxy.isPresent()) {
            logger.error("Username {} not found", clientId);
            throw new ClientRegistrationException("Username not found");
        }
        User user = userProxy.get();
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
