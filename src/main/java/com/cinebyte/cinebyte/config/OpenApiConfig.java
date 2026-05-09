package com.cinebyte.cinebyte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CineByte API")
                        .version("1.0.0")
                        .description("API REST para la aplicación CineByte. Proporciona datos de películas, series, noticias y más."));
    }
}
