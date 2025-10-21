package com.aiquizzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("AI Quizzer API")
                .description("A comprehensive API for creating and managing AI-generated quizzes")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Dev")
                    .email("devkothiya412@gmail.com")
                    .url("https://any.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("Development server"),
                new Server()
                    .url("https://api.dev.com")
                    .description("Production server")
            ));
    }
}
