package com.rslakra.healthcare.routinecheckup.service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Debug filter to log login request parameters
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Component
@Slf4j
public class LoginDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Only log login POST requests
        if ("POST".equalsIgnoreCase(method) && requestURI.contains("/login")) {
            log.info("=== LOGIN REQUEST DEBUG ===");
            log.info("Request URI: {}", requestURI);
            log.info("Method: {}", method);
            
            // Log all request parameters
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                // Don't log password in full for security
                if ("password".equals(paramName)) {
                    log.info("Parameter: {} = {} (length: {})", paramName, "***", paramValue != null ? paramValue.length() : 0);
                } else {
                    log.info("Parameter: {} = {}", paramName, paramValue);
                }
            }
            
            // Log headers that might be relevant
            log.info("Content-Type: {}", request.getContentType());
            log.info("===========================");
        }
        
        filterChain.doFilter(request, response);
    }
}

