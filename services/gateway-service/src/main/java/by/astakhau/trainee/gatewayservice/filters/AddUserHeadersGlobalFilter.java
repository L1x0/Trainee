package by.astakhau.trainee.gatewayservice.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class AddUserHeadersGlobalFilter implements GlobalFilter, Ordered {

    private static final String COOKIE_ACCESS = "access_token";
    private final ReactiveJwtDecoder jwtDecoder;

    @Autowired
    public AddUserHeadersGlobalFilter(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public int getOrder() { return Ordered.LOWEST_PRECEDENCE - 10; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/auth")) { // не вмешиваемся в login/refresh
            return chain.filter(exchange);
        }

        // берем Authorization header или cookie
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            HttpCookie cookie = exchange.getRequest().getCookies().getFirst(COOKIE_ACCESS);
            if (cookie != null) {
                authHeader = "Bearer " + cookie.getValue();
            }
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // нет токена — просто пропускаем (security дальше решит)
            return chain.filter(exchange);
        }

        String token = authHeader.substring("Bearer ".length());
        return jwtDecoder.decode(token)
                .flatMap(jwt -> {
                    String userId = jwt.getSubject();
                    String roles = extractRoles(jwt);
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-Roles", roles)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(ex -> {
                    // при ошибке валидации токена — не добавляем заголовки, пусть security вернёт 401
                    log.debug("JWT decode failed in gateway filter: {}", ex.getMessage());
                    return chain.filter(exchange);
                });
    }

    private String extractRoles(Jwt jwt) {
        // собираем роли из realm_access и resource_access
        String realmRoles = "";
        Object ra = jwt.getClaims().get("realm_access");
        if (ra instanceof Map) {
            Object rolesObj = ((Map<?,?>) ra).get("roles");
            if (rolesObj instanceof Collection) {
                realmRoles = ((Collection<?>) rolesObj).stream().map(Object::toString).collect(Collectors.joining(","));
            }
        }

        String resRoles = "";
        Object rao = jwt.getClaims().get("resource_access");
        if (rao instanceof Map) {
            resRoles = ((Map<?,?>) rao).values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .flatMap(m -> {
                        Object r = m.get("roles");
                        if (r instanceof Collection) {
                            return ((Collection<?>) r).stream().map(Object::toString);
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.joining(","));
        }

        return Stream.of(realmRoles, resRoles).filter(s -> !s.isEmpty()).collect(Collectors.joining(","));
    }
}
