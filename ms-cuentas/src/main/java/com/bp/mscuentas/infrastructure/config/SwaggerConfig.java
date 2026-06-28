package com.bp.mscuentas.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Cuentas API")
                        .version("1.0.0")
                        .description("Microservicio de Cuentas y Movimientos — Prueba Tecnica BP")
                        .contact(new Contact()
                                .name("Juan Rosendo Molina Leon")
                                .email("juan.molina@bp.com")));
    }
}