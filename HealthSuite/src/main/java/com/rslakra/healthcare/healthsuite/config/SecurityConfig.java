package com.rslakra.healthcare.healthsuite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

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

    @Autowired
    private DataSource dataSource;

    /**
     * Password encoder - using NoOpPasswordEncoder for plain text passwords.
     * Note: This is for development only. Use BCryptPasswordEncoder in production.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * UserDetailsService using JDBC.
     * Uses custom queries to join user_roles and roles tables instead of authorities table.
     * Spring Security expects authorities to be prefixed with "ROLE_", so we add that prefix.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl jdbcUserDetailsService = new JdbcDaoImpl();
        jdbcUserDetailsService.setDataSource(dataSource);
        
        // Custom query to load user by username
        jdbcUserDetailsService.setUsersByUsernameQuery(
            "SELECT username, password, enabled FROM users WHERE username = ?"
        );
        
        // Custom query to load authorities by joining user_roles and roles tables
        // Spring Security expects "ROLE_" prefix, so we add it
        // Using || operator for H2 database compatibility (also works with CONCAT)
        jdbcUserDetailsService.setAuthoritiesByUsernameQuery(
            "SELECT u.username, 'ROLE_' || r.name as authority " +
            "FROM users u " +
            "INNER JOIN user_roles ur ON u.id = ur.user_id " +
            "INNER JOIN roles r ON ur.role_id = r.id " +
            "WHERE u.username = ?"
        );
        
        return jdbcUserDetailsService;
    }

    /**
     * AuthenticationProvider using JDBC UserDetailsService.
     * This configures authentication with the password encoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, 
                                                             PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
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
        // Use AntPathRequestMatcher explicitly because there are multiple servlets (H2 console and DispatcherServlet)
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/about"),
                    new AntPathRequestMatcher("/contact"),
                    new AntPathRequestMatcher("/contact-us"),
                    new AntPathRequestMatcher("/h2/**"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/login/**"),
                    new AntPathRequestMatcher("/jquery-1.8.3.js"),
                    new AntPathRequestMatcher("/favicon.ico"),
                    new AntPathRequestMatcher("/assets/**"),
                    new AntPathRequestMatcher("/pdfs/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**")
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    new AntPathRequestMatcher("/h2/**"),
                    new AntPathRequestMatcher("/h2-console/**")
                )
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

