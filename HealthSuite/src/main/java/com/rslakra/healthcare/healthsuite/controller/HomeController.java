package com.rslakra.healthcare.healthsuite.controller;

import com.rslakra.healthcare.healthsuite.model.User;
import com.rslakra.healthcare.healthsuite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

/**
 * Controller for the home, about, and contact pages.
 * 
 * @author rslakra
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserService userService;

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
    public String login(Model model) {
        LOGGER.debug("+login({})", model);
        
        // Check if user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            LOGGER.debug("-login(), user already authenticated, redirecting to home");
            return "redirect:/";
        }

        model.addAttribute("authenticated", false);
        
        LOGGER.debug("-login(), returning login page");
        return "login";
    }

    /**
     * Display the registration page (public access).
     */
    @RequestMapping(value = {"/register", "/add-registration"}, method = RequestMethod.GET)
    public String registerForm(Model model) {
        LOGGER.debug("+registerForm({})", model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !authentication.getName().equals("anonymousUser")) {
            LOGGER.debug("-registerForm(), user already authenticated, redirecting to home");
            return "redirect:/";
        }

        model.addAttribute("authenticated", false);
        model.addAttribute("user", new User());
        LOGGER.debug("-registerForm(), returning register page");
        return "register";
    }

    /**
     * Process registration form submission (public access).
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") User user,
                           @RequestParam("confirmPassword") String confirmPassword,
                           BindingResult result,
                           Model model) {
        LOGGER.debug("+register({}, {})", user, result);

        model.addAttribute("authenticated", false);

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            result.rejectValue("password", "NotBlank", "Password is required");
        } else if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "Match", "Passwords do not match");
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "Duplicate", "Username is already taken");
        }

        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "Duplicate", "Email is already registered");
        }

        if (result.hasErrors()) {
            LOGGER.debug("Registration validation errors: {}", result.getAllErrors());
            return "register";
        }

        try {
            if (!userService.registerUser(user)) {
                model.addAttribute("error", "Registration failed. Please try again.");
                return "register";
            }
        } catch (Exception e) {
            LOGGER.error("Error during registration: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred during registration.");
            return "register";
        }

        LOGGER.debug("-register(), redirecting to login");
        return "redirect:/login?registered=true";
    }

    /**
     * Display the reset password page (public access).
     */
    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public String resetPasswordForm(Model model) {
        LOGGER.debug("+resetPasswordForm({})", model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !authentication.getName().equals("anonymousUser")) {
            LOGGER.debug("-resetPasswordForm(), user already authenticated, redirecting to home");
            return "redirect:/";
        }

        model.addAttribute("authenticated", false);
        LOGGER.debug("-resetPasswordForm(), returning reset-password page");
        return "reset-password";
    }

    /**
     * Process reset password form submission (public access).
     */
    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public String resetPassword(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        LOGGER.debug("+resetPassword(username={})", username);

        model.addAttribute("authenticated", false);

        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("usernameError", "Username is required");
        } else if (!userService.existsByUsername(username.trim())) {
            model.addAttribute("usernameError", "No account found with that username");
        }

        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("passwordError", "New password is required");
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("passwordError", "Passwords do not match");
        }

        if (model.containsAttribute("usernameError") || model.containsAttribute("passwordError")) {
            model.addAttribute("username", username);
            return "reset-password";
        }

        try {
            if (!userService.resetPassword(username.trim(), password)) {
                model.addAttribute("error", "Password reset failed. Please try again.");
                model.addAttribute("username", username);
                return "reset-password";
            }
        } catch (Exception e) {
            LOGGER.error("Error during password reset: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while resetting your password.");
            model.addAttribute("username", username);
            return "reset-password";
        }

        LOGGER.debug("-resetPassword(), redirecting to login");
        return "redirect:/login?reset=true";
    }
}

