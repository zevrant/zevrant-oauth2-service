package com.zevrant.services.zevrantoauth2service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UsersService {

    private final WebClient webClient;
    private final String keycloakUrl;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public UsersService(WebClient keycloakClient,
                        @Value("${zevrant.services.keycloak.baseUrl}") String keycloakUrl,
                        @Value("${oauth.admin.clientId}") String clientId,
                        @Value("${oauth.admin.clientSecret}") String clientSecret) {
        this.webClient = keycloakClient;
        this.keycloakUrl = keycloakUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

//    public Flux<KeycloakUser> getUsersByUsername(String username) {
//            return webClient.post()
//                    .uri("/realms/master/protocol/openid-connect/token")
//                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                    .body(BodyInserters
//                            .fromFormData("client_id", clientId)
//                            .with("client_secret", clientSecret)
//                            .with("grant_type", "client_credentials")
//                            .with("scope", "openid"))
//                    .retrieve()
//                    .bodyToMono(OAuthToken.class)
//                    .map(response -> {
//                        if(response == null || StringUtils.isBlank(response.getAccessToken())) {
//                            throw new RuntimeException("Failed to retrieve oauth2 access token");
//                        }
//                        try {
//                            return webClient
//                                    .get()
//                                    .uri("/admin/realms/zevrant-services/users?username="
//                                            .concat(URLEncoder.encode(username, String.valueOf(StandardCharsets.UTF_8))))
//                                    .header(HttpHeaders.AUTHORIZATION, "Bearer ".concat(response.getAccessToken()))
//                                    .retrieve()
//                                    .bodyToFlux(KeycloakUser.class);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    });
//    }
//
//    public Object getRolesForUser(KeycloakUser keycloakUser) {
//
//    }
}
