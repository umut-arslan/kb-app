package com.kb.template.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile({"dev", "test"})
public class WebConfigDev {

    @Autowired
    private AuthHandler successHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeHttpRequests()
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