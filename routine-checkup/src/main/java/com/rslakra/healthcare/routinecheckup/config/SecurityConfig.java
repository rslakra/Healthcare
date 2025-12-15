package com.rslakra.healthcare.routinecheckup.config;

import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import com.rslakra.healthcare.routinecheckup.utils.security.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import jakarta.servlet.Filter;

/**
 * Spring Security configuration class for the Routine Checkup healthcare application.
 * 
 * <p>This class configures:
 * <ul>
 *   <li>HTTP security rules and authorization policies</li>
 *   <li>Authentication mechanisms (form login, JWT)</li>
 *   <li>Session management (stateless with JWT)</li>
 *   <li>CSRF protection</li>
 *   <li>Custom security filters (JWT, login attempt tracking, request logging)</li>
 * </ul>
 * 
 * <p>Security roles supported:
 * <ul>
 *   <li>ADMIN - Full administrative access</li>
 *   <li>DOCTOR - Medical professional access</li>
 *   <li>NURSE - Nursing staff access</li>
 *   <li>PATIENT - Patient access</li>
 * </ul>
 * 
 * <p>Public endpoints (no authentication required):
 * <ul>
 *   <li>Static resources (CSS, JS, images)</li>
 *   <li>H2 console (development only)</li>
 *   <li>Login and registration pages</li>
 * </ul>
 * 
 * @author Rohtash Lakra
 * @created 8/12/21 3:56 PM
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** Service for user-related operations (not directly used in this config, but available for future use). */
    private final UserService userService;
    
    /** Password encoder for hashing and verifying user passwords. */
    private final PasswordEncoder passwordEncoder;
    
    /** Custom handler for successful authentication events. */
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    /** Custom handler for failed authentication attempts. */
    private final AuthenticationFailureHandler customAuthenticationFailureHandler;
    
    /**
     * Filter for JWT token-based authentication.
     * Note: jakarta.servlet.Filter is not a generic interface, so raw type is required.
     */
    private final Filter jwtAuthenticationFilter;
    
    /**
     * Filter for tracking and limiting login attempt counts.
     * Note: jakarta.servlet.Filter is not a generic interface, so raw type is required.
     */
    private final Filter loginAttemptCountFilter;
    
    /**
     * Filter for logging HTTP requests for security auditing.
     * Note: jakarta.servlet.Filter is not a generic interface, so raw type is required.
     */
    private final Filter requestLoggingFilter;
    
    /** Custom handler for successful logout events. */
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    
    /** Custom CSRF token repository for managing CSRF tokens. */
    private final CsrfTokenRepository customCsrfTokenRepository;
    
    /**
     * Filter for debugging login-related issues (development/testing).
     * Note: jakarta.servlet.Filter is not a generic interface, so raw type is required.
     */
    private final Filter loginDebugFilter;

    /**
     * Configures the security filter chain for the application.
     * 
     * <p>This method sets up:
     * <ul>
     *   <li>Authorization rules based on user roles</li>
     *   <li>Form-based login configuration</li>
     *   <li>Logout handling</li>
     *   <li>CSRF protection with custom token repository</li>
     *   <li>Stateless session management (for JWT-based authentication)</li>
     *   <li>Custom security filters in the correct order</li>
     * </ul>
     * 
     * <p>Filter order:
     * <ol>
     *   <li>Request logging filter</li>
     *   <li>Login attempt count filter</li>
     *   <li>Login debug filter</li>
     *   <li>Username/password authentication filter</li>
     *   <li>JWT authentication filter</li>
     * </ol>
     * 
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
                        // Permit H2 console (for development - disable in production)
                        .requestMatchers("/h2/**", "/h2/**").permitAll()
                        // Permit login and registration
                        .requestMatchers(HttpMethod.POST, ViewNames.LOGIN_URL).permitAll()
                        .requestMatchers(ViewNames.REGISTRATION_URL).permitAll()
                        .requestMatchers(ViewNames.REGISTRATION_URL + "/**").permitAll()
                        // Permit admin utility endpoints (for development/testing - disable in production)
                        .requestMatchers("/admin/**").permitAll()
                               // Admin paths
                               .requestMatchers(ViewNames.ADMIN_BASE_PATH + "**").hasRole(Roles.ADMIN.getValue())
                               // All other paths require authentication (PATIENT, DOCTOR, NURSE all have access)
                               .requestMatchers("/**").hasAnyRole(
                                       Roles.PATIENT.getValue(),
                                       Roles.DOCTOR.getValue(),
                                       Roles.NURSE.getValue()
                               )
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(ViewNames.LOGIN_URL)
                        .usernameParameter("username")  // Explicitly set username parameter name
                        .passwordParameter("password")   // Explicitly set password parameter name
                        .loginProcessingUrl(ViewNames.LOGIN_URL)  // Explicitly set login processing URL
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)  // Use custom failure handler
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(ViewNames.LOGOUT_URL)
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .permitAll()
                )
                // Disabled HTTPS requirement for development (SSL is disabled)
                // Uncomment the next line when SSL is enabled in production:
                // .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(customCsrfTokenRepository)
                        .ignoringRequestMatchers(
                                ViewNames.LOGIN_URL,
                                ViewNames.REGISTRATION_URL,
                                "/h2/**",
                                "/h2/**"
                        )
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                )
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginAttemptCountFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginDebugFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates and configures the AuthenticationManager bean.
     * 
     * <p>This method sets up a DAO-based authentication provider that:
     * <ul>
     *   <li>Uses the provided UserDetailsService to load user details</li>
     *   <li>Uses the provided PasswordEncoder to verify passwords</li>
     *   <li>Returns a ProviderManager with the configured authentication provider</li>
     * </ul>
     * 
     * @param userDetailsService the service to load user details
     * @param passwordEncoder the encoder to verify passwords
     * @return the configured AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

}
