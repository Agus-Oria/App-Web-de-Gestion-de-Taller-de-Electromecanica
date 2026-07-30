package com.taller.gestion_taller.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionTallerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gestion Taller API")
                        .version("v1")
                        .description("API REST para gestion de taller electromecanico"));
    }
}
