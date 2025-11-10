package com.rslakra.healthcare.healthsuite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the home, about, and contact pages.
 * 
 * @author rslakra
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    /**
     * Display the home page (public access).
     * 
     * @param model the model
     * @return the home page view
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        LOGGER.debug("+home({})", model);
        
        // Get current authenticated user info (if authenticated)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        
        LOGGER.debug("-home(), model={}", model);
        return "index";
    }

    /**
     * Display the about page (public access).
     * 
     * @param model the model
     * @return the about page view
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(Model model) {
        LOGGER.debug("+about({})", model);
        
        // Get current authenticated user info (if authenticated)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        
        LOGGER.debug("-about(), model={}", model);
        return "about";
    }

    /**
     * Display the contact page (public access).
     * 
     * @param model the model
     * @return the contact page view
     */
    @RequestMapping(value = {"/contact", "/contact-us"}, method = RequestMethod.GET)
    public String contact(Model model) {
        LOGGER.debug("+contact({})", model);
        
        // Get current authenticated user info (if authenticated)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        
        LOGGER.debug("-contact(), model={}", model);
        return "contact";
    }

    /**
     * Handle contact form submission (public access).
     * 
     * @param name the sender's name
     * @param email the sender's email
     * @param subject the message subject
     * @param message the message content
     * @param model the model
     * @return redirect to contact page with success message
     */
    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String submitContact(
            @org.springframework.web.bind.annotation.RequestParam("name") String name,
            @org.springframework.web.bind.annotation.RequestParam("email") String email,
            @org.springframework.web.bind.annotation.RequestParam("subject") String subject,
            @org.springframework.web.bind.annotation.RequestParam("message") String message,
            Model model) {
        LOGGER.debug("+submitContact(name={}, email={}, subject={}, message={})", 
                     name, email, subject, message);
        
        // Log the contact form submission (in a real application, you would send an email or save to database)
        LOGGER.info("Contact form submitted - Name: {}, Email: {}, Subject: {}, Message: {}", 
                    name, email, subject, message);
        
        // Get current authenticated user info (if authenticated)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        
        // Add success message
        model.addAttribute("success", true);
        model.addAttribute("successMessage", "Thank you for contacting us! We'll get back to you soon.");
        
        LOGGER.debug("-submitContact(), returning contact page with success message");
        return "contact";
    }

    /**
     * Display the login page (public access).
     * If user is already authenticated, redirect to home.
     * 
     * @return the login page view or redirect to home
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        LOGGER.debug("+login()");
        
        // Check if user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            LOGGER.debug("-login(), user already authenticated, redirecting to home");
            return "redirect:/";
        }
        
        LOGGER.debug("-login(), returning login page");
        return "login";
    }
}

