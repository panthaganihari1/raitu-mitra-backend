package com.raitu.mitra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("🌾 Raitu Mitra API")
                .description("""
                    **రైతు మిత్ర** — Rural Farmer Marketplace API
                    
                    Features:
                    - 🐓 Buy & Sell livestock (Hens, Goats, Cows, Buffaloes)
                    - 🌾 Daily crop prices from mandis
                    - 🌤️ District-wise weather for farmers
                    - 📍 Location-based nearby listings
                    - 🔐 Phone number based authentication with JWT
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Raitu Mitra Team")
                    .email("support@raitu-mitra.com")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter your JWT token")));
    }
}
