package by.astakhau.trainee.gatewayservice.services;

import by.astakhau.trainee.gatewayservice.dtos.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TokenService {

    private final WebClient webClient;
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;

    public TokenService(@Value("${keycloak.token-uri}") String tokenUri,
                        @Value("${keycloak.client-id}") String clientId,
                        @Value("${keycloak.client-secret:}") String clientSecret,
                        WebClient.Builder builder) {
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = builder.build();
    }

    public Mono<TokenResponse> passwordGrant(String username, String password) {
        BodyInserters.FormInserter<String> form = BodyInserters.fromFormData("grant_type", "password")
                .with("username", username)
                .with("password", password)
                .with("client_id", clientId);

        if (clientSecret != null && !clientSecret.isBlank()) {
            form = form.with("client_secret", clientSecret);
        }

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        BodyInserters.FormInserter<String> form = BodyInserters.fromFormData("grant_type", "refresh_token")
                .with("refresh_token", refreshToken)
                .with("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form = form.with("client_secret", clientSecret);
        }

        return webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }
}

