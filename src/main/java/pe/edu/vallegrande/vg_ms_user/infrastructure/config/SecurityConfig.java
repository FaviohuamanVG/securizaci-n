package pe.edu.vallegrande.vg_ms_user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        // Permitir acceso sin autenticación a endpoints de salud y documentación
                        .pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .pathMatchers("/api/v1/health").permitAll()  // Endpoint de salud público
                        
                        // Endpoints que requieren rol de administrador
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ADMIN", "USER")
                        
                        // Endpoints de permisos - solo admin
                        .pathMatchers("/api/v1/permissions/**").hasRole("ADMIN")
                        
                        // Endpoints de profesores - admin y user pueden ver, solo admin puede modificar
                        .pathMatchers(HttpMethod.GET, "/api/v1/teachers/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/teachers/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/teachers/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/teachers/**").hasRole("ADMIN")
                        
                        // Endpoints de sedes de usuario
                        .pathMatchers(HttpMethod.GET, "/api/v1/user-sedes/**").hasAnyRole("ADMIN", "USER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/user-sedes/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/user-sedes/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/user-sedes/**").hasRole("ADMIN")
                        
                        // Cualquier otra petición debe estar autenticada
                        .anyExchange().authenticated()
                        
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    private Flux<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extraer roles de Keycloak del realm_access
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            return Flux.fromIterable(roles)
                    .filter(role -> role.equals("admin") || role.equals("user") || role.startsWith("eduassist-"))
                    .map(role -> {
                        // Mapear roles específicos
                        if (role.equals("admin")) {
                            return new SimpleGrantedAuthority("ROLE_ADMIN");
                        } else if (role.equals("user")) {
                            return new SimpleGrantedAuthority("ROLE_USER");
                        } else if (role.startsWith("eduassist-")) {
                            // Remover prefijo para roles con prefijo
                            String cleanRole = role.replace("eduassist-", "");
                            return new SimpleGrantedAuthority("ROLE_" + cleanRole.toUpperCase());
                        }
                        return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    })
                    .cast(GrantedAuthority.class);
        }

        // También extraer roles del cliente específico si existe
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("eduassist")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("eduassist");
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> clientRoles = (List<String>) clientAccess.get("roles");
                return Flux.fromIterable(clientRoles)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .cast(GrantedAuthority.class);
            }
        }

        return Flux.empty();
    }
}
