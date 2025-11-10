package com.rslakra.healthcare.healthsuite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for handling the home page.
 * 
 * @author rslakra
 */
@Controller
public class HomeController {

    /**
     * Handles the root path and returns the index view.
     * The view name "index" will be resolved to /WEB-INF/jsp/index.jsp
     * by the InternalResourceViewResolver configured in WebMvcConfig.
     * 
     * @return view name "index" which resolves to index.jsp
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }
}

