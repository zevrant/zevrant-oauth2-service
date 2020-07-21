package net.zevrant.services.zevrant.oauth2.service.config;


import net.zevrant.services.zevrant.oauth2.service.entity.Token;
import net.zevrant.services.zevrant.oauth2.service.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ZevrantOauthResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final TokenRepository tokenRepository;
    private final DefaultAuthorizationCodeTokenResponseClient defaultClient;

    @Autowired
    public ZevrantOauthResponseClient(TokenRepository userRepository) {
        this.tokenRepository = userRepository;
        this.defaultClient = new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        Optional<Token> isPresent = tokenRepository.findById(authorizationGrantRequest.getClientRegistration().getClientId().getBytes());
        isPresent.ifPresent(token -> tokenRepository.delete(token));
        OAuth2AccessTokenResponse response = defaultClient.getTokenResponse(authorizationGrantRequest);
        Token token = new Token();
        token.setToken(response.getAccessToken().getTokenValue());
        token.setClientId(authorizationGrantRequest.getClientRegistration().getClientId());
        tokenRepository.save(token);
        return response;
    }
}
