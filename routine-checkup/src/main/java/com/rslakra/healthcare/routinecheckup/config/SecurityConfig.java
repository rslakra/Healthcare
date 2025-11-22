package com.rslakra.healthcare.routinecheckup.config;

import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import com.rslakra.healthcare.routinecheckup.utils.security.RoleNames;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.Filter;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 3:56 PM
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final Filter jwtAuthenticationFilter;
    private final Filter loginAttemptCountFilter;
    private final LogoutSuccessHandler customLogoutSuccessHandler;
    private final CsrfTokenRepository customCsrfTokenRepository;

    /**
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Permit static resources
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**").permitAll()
                // Permit H2 console (for development - disable in production)
                .antMatchers("/h2/**", "/h2-console/**").permitAll()
                // Permit login and registration
                .antMatchers(HttpMethod.POST, ViewNames.LOGIN_URL).permitAll()
                .antMatchers(ViewNames.REGISTRATION_URL).permitAll()
                .antMatchers(ViewNames.REGISTRATION_URL + "/**")
                .permitAll()
                // Admin paths
                .antMatchers(ViewNames.ADMIN_BASE_PATH + "**")
                .hasRole(RoleNames.ADMIN.getValue())
                // All other paths require authentication
                .antMatchers("/**")
                .hasRole(RoleNames.USER.getValue())
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(ViewNames.LOGIN_URL)
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(ViewNames.LOGOUT_URL)
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll()
                .and()
                // Disabled HTTPS requirement for development (SSL is disabled)
                // Uncomment the next line when SSL is enabled in production:
                // .requiresChannel().anyRequest().requiresSecure()
                // .and()
                .csrf()
                .csrfTokenRepository(customCsrfTokenRepository)
                .ignoringAntMatchers(
                        ViewNames.LOGIN_URL,
                        ViewNames.REGISTRATION_URL,
                        "/h2/**",
                        "/h2-console/**"
                )
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                .and()
                // Disable frame options for H2 console
                .headers().frameOptions().sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        loginAttemptCountFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }

    /**
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

}
