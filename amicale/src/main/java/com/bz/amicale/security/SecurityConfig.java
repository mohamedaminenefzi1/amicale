package com.bz.amicale.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @PostConstruct
    public void init() {
        try {
            SSLUtil.turnOffSslChecking(); // Disables SSL checking (not recommended for production)
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to turn off SSL checking", e);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/admin/**").hasRole("admin") // Requires "ROLE_admin"
                    .requestMatchers("/user/**").hasRole("user")   // Requires "ROLE_user"
                    .requestMatchers("/").authenticated()          // Root path requires authentication
                    .anyRequest().authenticated()                  // All other requests require authentication
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)                     // Redirect to root after login
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // Matches your POST /logout endpoint
                .logoutSuccessUrl("http://localhost:8083/realms/amicale/protocol/openid-connect/logout?redirect_uri=http://localhost:8081/") // Keycloak logout with redirect
                .invalidateHttpSession(true)                      // Invalidate session
                .clearAuthentication(true)                        // Clear authentication
                .deleteCookies("JSESSIONID")                      // Remove session cookie
                .permitAll()                                      // Allow logout without authentication
            );

        return http.build();
    }
}