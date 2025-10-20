package by.astakhau.trainee.gatewayservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("expires_in")
    public Integer expiresIn;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("scope")
    public String scope;
}

