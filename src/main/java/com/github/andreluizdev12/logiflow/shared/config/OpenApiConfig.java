package com.github.andreluizdev12.logiflow.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logiflowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiFlow API")
                        .description("API para gerenciamento logístico")
                        .version("1.0.0"));
    }
}