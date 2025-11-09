package com.rslakra.healthcare.healthsuite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${server.servlet.contextPath:}")
    private String contextPath;

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
     * This replaces the XML-based jdbc-user-service.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcDaoImpl jdbcUserDetailsService = new JdbcDaoImpl();
        jdbcUserDetailsService.setDataSource(dataSource);
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
        String defaultSuccessUrl = contextPath.isEmpty() ? "/" : contextPath + "/";
        
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/h2/**", "/h2-console/**").permitAll()
                .anyRequest().hasRole("USER")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2/**", "/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            .formLogin(formLogin -> formLogin
                .defaultSuccessUrl(defaultSuccessUrl, true)
                .permitAll()
            );
        return http.build();
    }
}

