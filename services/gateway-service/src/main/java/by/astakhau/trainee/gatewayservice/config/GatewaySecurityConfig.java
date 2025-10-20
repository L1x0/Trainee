package by.astakhau.trainee.gatewayservice.config;

import by.astakhau.trainee.gatewayservice.services.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class GatewaySecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {


        Converter<Jwt, AbstractAuthenticationToken> jwtConverter = new KeycloakRoleConverter();
        var reactiveConverter = new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**", "/public/**", "/actuator/**").permitAll()
                        .pathMatchers("/drivers/**").hasAnyRole("driver", "admin")
                        .pathMatchers("/passenger/**").hasAnyRole("passenger", "admin")
                        .pathMatchers("/admin/**").hasRole("admin")
                        .pathMatchers("/trips/**").hasAnyRole("driver", "admin", "passenger")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveConverter))
                );

        return http.build();
    }
}
