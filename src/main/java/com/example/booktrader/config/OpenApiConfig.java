package com.example.booktrader.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // define que a api utilizara autenticacao com token
        return new OpenAPI()

                // adiciona a exigencia de autenticacao nas operacoes do swagger
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))

                // configura o tipo de autenticacao utilizado
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()

                                        // define a autenticacao pelo protocolo http
                                        .type(SecurityScheme.Type.HTTP)

                                        // informa que sera utilizado o bearer token
                                        .scheme("bearer")

                                        // identifica o token como jwt
                                        .bearerFormat("JWT")

                                        // informa que o token sera enviado no cabecalho
                                        .in(SecurityScheme.In.HEADER)

                                        // define o nome do cabecalho de autenticacao
                                        .name("Authorization")));
    }
}