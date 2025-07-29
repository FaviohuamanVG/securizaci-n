package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.vallegrande.vg_ms_user.infrastructure.config.JwtUserExtractor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRest {

    private final JwtUserExtractor jwtUserExtractor;

    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return Mono.fromCallable(() -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", jwt.getClaimAsString("sub"));
            userInfo.put("username", jwt.getClaimAsString("preferred_username"));
            userInfo.put("email", jwt.getClaimAsString("email"));
            userInfo.put("firstName", jwt.getClaimAsString("given_name"));
            userInfo.put("lastName", jwt.getClaimAsString("family_name"));
            userInfo.put("roles", jwt.getClaimAsMap("realm_access"));
            return userInfo;
        });
    }

    @GetMapping("/user-id")
    public Mono<String> getCurrentUserId() {
        return jwtUserExtractor.getCurrentUserId();
    }

    @GetMapping("/username")
    public Mono<String> getCurrentUsername() {
        return jwtUserExtractor.getCurrentUsername();
    }

    @GetMapping("/email")
    public Mono<String> getCurrentUserEmail() {
        return jwtUserExtractor.getCurrentUserEmail();
    }
}
