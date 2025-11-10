package com.rslakra.healthcare.healthsuite.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Controller to handle favicon requests.
 * Serves the favicon.ico file from /static/images/favicon.ico
 * 
 * @author rslakra
 */
@Controller
public class FaviconController {

    /**
     * Handles favicon.ico requests.
     * Returns the favicon.ico file from /static/images/favicon.ico
     * 
     * @return ResponseEntity containing the favicon resource
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Resource> favicon() {
        Resource resource = new ClassPathResource("static/images/favicon.ico");
        
        if (resource.exists() && resource.isReadable()) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("image/x-icon"));
                headers.setContentLength(resource.contentLength());
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } catch (IOException e) {
                // If we can't read the content length, still return the resource
                // Spring will handle the content length automatically
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("image/x-icon"));
                
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        }
        
        // If favicon doesn't exist, return 204 No Content
        return ResponseEntity.noContent().build();
    }
}

