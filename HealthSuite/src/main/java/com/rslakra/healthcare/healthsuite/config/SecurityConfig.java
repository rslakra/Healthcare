package com.rslakra.healthcare.healthsuite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for HealthSuite application.
 * This configuration allows H2 console access without authentication
 * and requires authentication for all other endpoints.
 * 
 * This Java-based configuration replaces the XML-based security configuration.
 * 
 * @author rslakra
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Plain-text password encoder for local development with H2 seed data.
     * Note: This is for development only. Use BCryptPasswordEncoder in production.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }

    /**
     * AuthenticationProvider using the application UserDetailsService.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                             PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Single security filter chain for all endpoints.
     * Allows unauthenticated access to H2 console endpoints.
     * Requires authentication for all other endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/",
                    "/about",
                    "/contact",
                    "/contact-us",
                    "/h2/**",
                    "/login",
                    "/login/**",
                    "/register",
                    "/add-registration",
                    "/reset-password",
                    "/jquery-1.8.3.js",
                    "/favicon.ico",
                    "/assets/**",
                    "/pdfs/**",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }
}
