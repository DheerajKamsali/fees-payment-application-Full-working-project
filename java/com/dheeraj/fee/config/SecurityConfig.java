
package com.dheeraj.fee.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.
        HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.
        EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.
        BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.
        UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/login.html",
                    "/register.html",
                    "/forgot-password.html",
                    "/reset-password.html",
                    "/admin.html",
                    "/dashboard.html",
                    "/css/**",
                    "/js/**"
                ).permitAll()

                // Auth APIs
                .requestMatchers("/auth/**").permitAll()

                // Admin APIs
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // All other requests require login
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
