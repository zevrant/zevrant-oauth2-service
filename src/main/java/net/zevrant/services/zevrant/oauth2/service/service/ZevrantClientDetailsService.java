package net.zevrant.services.zevrant.oauth2.service.service;

import net.zevrant.services.zevrant.oauth2.service.entity.User;
import net.zevrant.services.zevrant.oauth2.service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class ZevrantClientDetailsService implements ClientDetailsService, UserDetailsService {

    private UserRepository userRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return convertUser(userRepository.findByUsername(clientId));
    }

    private ClientDetails convertUser(User user) {
        return new net.zevrant.services.zevrant.oauth2.service.entity.ClientDetails(user.getUsername(), user.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) convertUser(userRepository.findByUsername(username));
    }
}
