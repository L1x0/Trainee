package by.astakhau.trainee.gatewayservice.services;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class KeycloakRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String,Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null && realmAccess.get("roles") instanceof Collection) {
            ((Collection<String>)realmAccess.get("roles"))
                    .forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_"+r)));
        }

        Map<String,Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .forEach(m -> {
                        Collection<String> roles = (Collection<String>) m.get("roles");
                        if (roles != null) roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_"+r)));
                    });
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
