package pe.edu.vallegrande.vg_ms_user.infrastructure.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public Mono<Map<String, Object>> health() {
        return Mono.just(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "vg-ms-user",
            "message", "Microservicio de usuarios funcionando correctamente"
        ));
    }

    @GetMapping("/secure")
    public Mono<Map<String, Object>> secureHealth() {
        return Mono.just(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "vg-ms-user",
            "message", "Endpoint seguro - usuario autenticado"
        ));
    }
}
