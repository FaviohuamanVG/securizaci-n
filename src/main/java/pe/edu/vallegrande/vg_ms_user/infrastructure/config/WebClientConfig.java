
package pe.edu.vallegrande.vg_ms_user.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Lee la URL del archivo application.yml, con un valor por defecto para evitar el crash
    @Value("${institution.api.base-url:https://ms.institution.machashop.top/api/v1}")
    private String institutionApiBaseUrl;

    @Bean
    public WebClient institutionWebClient() {
        return WebClient.builder()
                .baseUrl(institutionApiBaseUrl)
                .build();
    }
}
