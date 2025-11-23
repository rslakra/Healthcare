package com.rslakra.healthcare.routinecheckup.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Request logging filter to log all HTTP requests with timing, parameters, and response details
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final String[] SENSITIVE_PARAMS = {"password", "pwd", "pass", "token", "secret", "key"};

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Wrap request and response to enable reading body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        // Record start time
        long startTime = System.currentTimeMillis();
        Instant startInstant = Instant.now();
        String startTimestamp = TIMESTAMP_FORMATTER.format(startInstant);
        
        try {
            // Process the request
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Record end time
            long endTime = System.currentTimeMillis();
            Instant endInstant = Instant.now();
            String endTimestamp = TIMESTAMP_FORMATTER.format(endInstant);
            long duration = endTime - startTime;
            
            // Log request details
            logRequestDetails(wrappedRequest, wrappedResponse, startTimestamp, endTimestamp, duration);
            
            // Copy response body back to original response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestDetails(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            String startTimestamp,
            String endTimestamp,
            long duration
    ) {
        try {
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("\n");
            logMessage.append("═══════════════════════════════════════════════════════════════════════════════\n");
            logMessage.append("HTTP REQUEST LOG\n");
            logMessage.append("═══════════════════════════════════════════════════════════════════════════════\n");
            
            // Request Information
            logMessage.append("REQUEST:\n");
            logMessage.append("  Method      : ").append(request.getMethod()).append("\n");
            logMessage.append("  URI         : ").append(request.getRequestURI()).append("\n");
            logMessage.append("  Query String: ").append(request.getQueryString() != null ? request.getQueryString() : "N/A").append("\n");
            logMessage.append("  Protocol    : ").append(request.getProtocol()).append("\n");
            logMessage.append("  Remote Addr : ").append(request.getRemoteAddr()).append("\n");
            logMessage.append("  Remote Host : ").append(request.getRemoteHost()).append("\n");
            logMessage.append("  User Agent  : ").append(request.getHeader("User-Agent")).append("\n");
            
            // Timing Information
            logMessage.append("\nTIMING:\n");
            logMessage.append("  Start Time  : ").append(startTimestamp).append("\n");
            logMessage.append("  End Time    : ").append(endTimestamp).append("\n");
            logMessage.append("  Duration    : ").append(duration).append(" ms (").append(formatDuration(duration)).append(")\n");
            
            // Request Parameters
            Map<String, String> params = getRequestParameters(request);
            if (!params.isEmpty()) {
                logMessage.append("\nREQUEST PARAMETERS:\n");
                params.forEach((key, value) -> {
                    if (isSensitive(key)) {
                        logMessage.append("  ").append(key).append(" = ").append(maskSensitiveValue(value)).append("\n");
                    } else {
                        logMessage.append("  ").append(key).append(" = ").append(value).append("\n");
                    }
                });
            }
            
            // Request Headers (optional - can be enabled if needed)
            // logMessage.append("\nREQUEST HEADERS:\n");
            // Enumeration<String> headerNames = request.getHeaderNames();
            // while (headerNames.hasMoreElements()) {
            //     String headerName = headerNames.nextElement();
            //     logMessage.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
            // }
            
            // Response Information
            logMessage.append("\nRESPONSE:\n");
            logMessage.append("  Status Code : ").append(response.getStatus()).append("\n");
            logMessage.append("  Content Type: ").append(response.getContentType() != null ? response.getContentType() : "N/A").append("\n");
            logMessage.append("  Content Size: ").append(response.getContentSize()).append(" bytes\n");
            
            // Response Body (only for small responses to avoid log bloat)
            if (response.getContentSize() > 0 && response.getContentSize() < 1000) {
                try {
                    byte[] contentAsByteArray = response.getContentAsByteArray();
                    String responseBody = new String(contentAsByteArray, response.getCharacterEncoding());
                    if (responseBody.length() > 0) {
                        logMessage.append("  Body        : ").append(responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody).append("\n");
                    }
                } catch (UnsupportedEncodingException e) {
                    logMessage.append("  Body        : [Unable to decode response body]\n");
                }
            } else if (response.getContentSize() >= 1000) {
                logMessage.append("  Body        : [Response body too large to log (").append(response.getContentSize()).append(" bytes)]\n");
            }
            
            logMessage.append("═══════════════════════════════════════════════════════════════════════════════\n");
            
            // Log based on duration - use different log levels
            if (duration > 5000) {
                log.warn(logMessage.toString());
            } else if (duration > 1000) {
                log.info(logMessage.toString());
            } else {
                log.debug(logMessage.toString());
            }
            
        } catch (Exception e) {
            log.error("Error logging request details", e);
        }
    }

    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }
        return params;
    }

    private boolean isSensitive(String paramName) {
        if (paramName == null) {
            return false;
        }
        String lowerParamName = paramName.toLowerCase();
        for (String sensitive : SENSITIVE_PARAMS) {
            if (lowerParamName.contains(sensitive)) {
                return true;
            }
        }
        return false;
    }

    private String maskSensitiveValue(String value) {
        if (value == null || value.isEmpty()) {
            return "***";
        }
        if (value.length() <= 4) {
            return "****";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }

    private String formatDuration(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.2fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
}

