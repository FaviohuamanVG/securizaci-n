
package pe.edu.vallegrande.vg_ms_user.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_user.infrastructure.dto.HeadquarterResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HeadquarterClient {

    private final WebClient webClient;

    public HeadquarterClient(@Value("${headquarter.api.base-url:https://lab.vallegrande.edu.pe/school/ms-institution/api/v1}") String headquarterApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(headquarterApiUrl)
                .build();
    }

    public Flux<HeadquarterResponseDTO> getAllHeadquarters() {
        return webClient.get()
                .uri("/headquarters")
                .retrieve()
                .bodyToFlux(HeadquarterResponseDTO.class);
    }

    public Mono<HeadquarterResponseDTO> getHeadquarterById(String id) {
        return webClient.get()
                .uri("/headquarters/{id}", id)
                .retrieve()
                .bodyToMono(HeadquarterResponseDTO.class);
    }
}
