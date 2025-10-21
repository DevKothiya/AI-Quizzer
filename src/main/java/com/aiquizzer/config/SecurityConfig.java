package com.aiquizzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                .requestMatchers("/api/**").permitAll() // For demo purposes, allow all API access
                .requestMatchers("/actuator/**").permitAll() // Allow actuator endpoints
                .anyRequest().permitAll() // Allow all other requests for demo
            )
            .headers(headers -> headers.frameOptions().disable()) // For H2 console
            .cors(AbstractHttpConfigurer::disable); // Disable CORS for demo
        
        return http.build();
    }
}
