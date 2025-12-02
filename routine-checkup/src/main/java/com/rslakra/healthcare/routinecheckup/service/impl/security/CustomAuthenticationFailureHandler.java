package com.rslakra.healthcare.routinecheckup.service.impl.security;

import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom authentication failure handler to provide better error messages
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        String username = request.getParameter("username");
        String errorMessage = "Incorrect credentials!";
        
        // Determine error message based on exception type to avoid extra database calls
        // UsernameNotFoundException is thrown when user doesn't exist or is temporary
        if (exception instanceof UsernameNotFoundException) {
            if (username != null && !username.trim().isEmpty()) {
                // User was found but is temporary (we know this from loadUserByUsername)
                // Check exception message to distinguish between "not found" and "temporary"
                String exceptionMessage = exception.getMessage();
                if (exceptionMessage != null && exceptionMessage.contains(username)) {
                    // User exists but is temporary - avoid another DB call
                    errorMessage = "Please complete your registration by clicking the confirmation link sent to your email.";
                    log.warn("Login failed for user '{}': Account not activated (temporary account)", username);
                } else {
                    errorMessage = "User not found!";
                    log.warn("Login failed for user '{}': User not found", username);
                }
            }
        } else {
            // BadCredentialsException - password mismatch
            errorMessage = "Incorrect password!";
            if (username != null) {
                log.warn("Login failed for user '{}': Password mismatch", username);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.debug("Authentication failure handler completed in {} ms. Exception: {}", duration, exception.getClass().getSimpleName());
        
        // Redirect to login page with error message
        String redirectUrl = ViewNames.LOGIN_URL + "?error=true&message=" + 
                java.net.URLEncoder.encode(errorMessage, java.nio.charset.StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}

