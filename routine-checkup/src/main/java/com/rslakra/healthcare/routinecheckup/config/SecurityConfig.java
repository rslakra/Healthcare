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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import jakarta.servlet.Filter;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 3:56 PM
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final Filter jwtAuthenticationFilter;
    private final Filter loginAttemptCountFilter;
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    private final CsrfTokenRepository customCsrfTokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
                        // Permit H2 console (for development - disable in production)
                        .requestMatchers("/h2/**", "/h2-console/**").permitAll()
                        // Permit login and registration
                        .requestMatchers(HttpMethod.POST, ViewNames.LOGIN_URL).permitAll()
                        .requestMatchers(ViewNames.REGISTRATION_URL).permitAll()
                        .requestMatchers(ViewNames.REGISTRATION_URL + "/**").permitAll()
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
                        .successHandler(customAuthenticationSuccessHandler)
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
                                "/h2-console/**"
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
                .addFilterBefore(loginAttemptCountFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

}
