package com.cateringmarketplace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for API documentation.
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.name:Catering Marketplace Platform}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName + " API")
                        .version(appVersion)
                        .description("Production-ready REST API for Catering Marketplace with Bidding System. " +
                                "This platform enables customers to post catering requirements and vendors to bid competitively.")
                        .termsOfService("https://cateringplatform.com/terms")
                        .contact(new Contact()
                                .name("Catering Platform Team")
                                .email("support@cateringplatform.com")
                                .url("https://cateringplatform.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://cateringplatform.com/license")))
                .servers(List.of(
                        new Server().url("/api/v1").description("Current Server"),
                        new Server().url("http://localhost:8080/api/v1").description("Local Development"),
                        new Server().url("https://staging-api.cateringplatform.com/api/v1").description("Staging Server"),
                        new Server().url("https://api.cateringplatform.com/api/v1").description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained from /auth/login endpoint")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

