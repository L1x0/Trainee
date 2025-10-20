package by.astakhau.trainee.gatewayservice.controllers;

import by.astakhau.trainee.gatewayservice.dtos.LoginRequest;
import by.astakhau.trainee.gatewayservice.dtos.RefreshRequest;
import by.astakhau.trainee.gatewayservice.dtos.TokenResponse;
import by.astakhau.trainee.gatewayservice.services.TokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@RequestBody LoginRequest req) {
        return tokenService.passwordGrant(req.username, req.password)
                .map(tr -> {
                    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tr.refreshToken == null ? "" : tr.refreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/auth")
                            .maxAge(tr.expiresIn != null ? tr.expiresIn : 86400)
                            .sameSite("Strict")
                            .build();

                    ResponseCookie accessCookie = ResponseCookie.from("access_token", tr.accessToken == null ? "" : tr.accessToken)
                            .httpOnly(true)
                            .secure(true)
                            .path("/auth")
                            .maxAge(tr.expiresIn != null ? tr.expiresIn : 300)
                            .sameSite("Strict")
                            .build();

                    return ResponseEntity.ok()
                            .header("Set-Cookie", refreshCookie.toString())
                            .header("Set-Cookie", accessCookie.toString())
                            .body(tr);
                });
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponse>> refresh(@CookieValue(name = "refresh_token", required = false) String refreshCookie,
                                                       @RequestBody(required = false) RefreshRequest rr) {
        String refreshToken = (rr != null && rr.refreshToken != null) ? rr.refreshToken : refreshCookie;
        if (refreshToken == null || refreshToken.isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return tokenService.refreshToken(refreshToken)
                .map(ResponseEntity::ok);
    }
}

