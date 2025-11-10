package com.rslakra.healthcare.healthsuite.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to handle favicon requests.
 * Returns 204 No Content to prevent 404 errors in browser console.
 * 
 * @author rslakra
 */
@Controller
public class FaviconController {

    /**
     * Handles favicon.ico requests.
     * Returns 204 No Content to indicate no favicon is available.
     * 
     * @return 204 No Content response
     */
    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public ResponseEntity<Void> favicon() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

