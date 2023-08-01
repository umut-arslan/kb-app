package com.kb.template.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@Profile("prod")
public class WebConfigProd{

    @Value("baseUrl")
    private String baseUrl;

    @Autowired
    private AuthHandler successHandler;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins(baseUrl);
            }
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
            .requestMatchers("/api/user", "/api/configs").authenticated()
            .anyRequest().permitAll()
            .and()
            .oauth2Login()
            .authorizationEndpoint().baseUri("/authorization")
            .and()
            .redirectionEndpoint().baseUri("/login")
            .and()
            .successHandler(successHandler)
            .and().logout().logoutSuccessUrl("/").permitAll();
        return http.build();
    }
}