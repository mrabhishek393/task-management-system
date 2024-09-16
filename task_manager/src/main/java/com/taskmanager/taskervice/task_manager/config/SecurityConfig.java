package com.taskmanager.taskervice.task_manager.config;

import com.taskmanager.taskervice.task_manager.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/tasks/**").authenticated()  // Secure task endpoints
                        .anyRequest().authenticated())  // Any other requests must be authenticated
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions (JWT)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter before the authentication filter

        return http.build();
    }
}