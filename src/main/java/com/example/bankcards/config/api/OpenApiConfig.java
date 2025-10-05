package com.example.bankcards.config.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank Rest API",
                description = "API для сервиса управления банковскими картами",
                version = "v1.0",
                contact = @Contact(
                        name = "Roman",
                        email = "mrnght8@gmail.com"
                )
        )
)
public class OpenApiConfig {
}