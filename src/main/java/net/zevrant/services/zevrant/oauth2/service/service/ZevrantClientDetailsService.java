package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.exceptions.UserNotFoundException;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ZevrantClientDetailsService implements ClientDetailsService, UserDetailsService {

    private UserRepository userRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<User> userProxy = userRepository.findByUsername(clientId);
        if(userProxy.isEmpty()){
            throw new UserNotFoundException("User " + clientId + " not found");
        }
        return convertUser(userProxy.get());
    }

    private ClientDetails convertUser(User user) {
        return new net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails(user.getUsername(), user.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) loadClientByClientId(username);
    }


}
