package pe.edu.vallegrande.vg_ms_user.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_user.infrastructure.dto.InstitutionResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InstitutionClient {

    private final WebClient webClient;

    public InstitutionClient(@Value("${external.service.institution-api:https://lab.vallegrande.edu.pe/school/ms-institution/api/v1}") String institutionApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(institutionApiUrl)
                .build();
    }

    public Flux<InstitutionResponseDTO> getAllInstitutions() {
        return webClient.get()
                .uri("/institutions")
                .retrieve()
                .bodyToFlux(InstitutionResponseDTO.class);
    }

    public Mono<InstitutionResponseDTO> getInstitutionById(String id) {
        return webClient.get()
                .uri("/institutions/{id}", id)
                .retrieve()
                .bodyToMono(InstitutionResponseDTO.class);
    }
}
