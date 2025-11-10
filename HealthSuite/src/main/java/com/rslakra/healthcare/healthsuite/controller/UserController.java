package com.rslakra.healthcare.healthsuite.controller;

import com.rslakra.healthcare.healthsuite.model.Registration;
import com.rslakra.healthcare.healthsuite.model.User;
import com.rslakra.healthcare.healthsuite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling user management and registration requests.
 * 
 * @author rslakra
 */
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Add authentication info to model for all requests.
     * 
     * @param model the model
     */
    @ModelAttribute
    public void addAuthenticationInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
    }

    /**
     * Display the add user form.
     * 
     * @param model the model
     * @return the add user form view
     */
    @RequestMapping(value = "/add-users", method = RequestMethod.GET)
    public String addUsers(Model model) {
        LOGGER.debug("+addUsers({})", model);
        User user = new User();
        model.addAttribute("user", user);
        LOGGER.debug("-addUsers(), model={}", model);
        return "addUsers";
    }

    /**
     * Process the add user form submission.
     * 
     * @param user the user data
     * @param result the binding result
     * @param model the model
     * @return redirect to home page on success, or return to form on error
     */
    @RequestMapping(value = "/add-users", method = RequestMethod.POST)
    public String processUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             Model model) {
        LOGGER.debug("+processUser({}, {})", user, result);

        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "addUsers";
        }

        LOGGER.info("User submitted: Username={}, Email={}, FirstName={}, LastName={}",
                user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName());

        try {
            boolean saved = userService.saveUser(user);
            if (!saved) {
                LOGGER.error("Failed to save user profile");
                model.addAttribute("error", "Failed to save user profile. Please try again.");
                return "addUsers";
            }
            LOGGER.info("User profile saved successfully");
        } catch (Exception e) {
            LOGGER.error("Error saving user profile: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while saving the user profile.");
            return "addUsers";
        }

        LOGGER.debug("-processUser(), redirecting to home page");
        return "redirect:/";
    }

    /**
     * REST API endpoint to create a user.
     * Accepts JSON in request body.
     * 
     * @param user the user data
     * @return ResponseEntity with success or error message
     */
    @RequestMapping(value = "/api/users", method = RequestMethod.POST,
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user) {
        LOGGER.debug("+createUser({})", user);

        Map<String, Object> response = new HashMap<>();

        LOGGER.info("User API call: Username={}, Email={}, FirstName={}, LastName={}",
                user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName());

        try {
            boolean saved = userService.saveUser(user);
            if (!saved) {
                response.put("success", false);
                response.put("message", "Failed to save user profile");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("data", user);
        } catch (Exception e) {
            LOGGER.error("Error saving user profile: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while saving the user profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        LOGGER.debug("-createUser()");
        return ResponseEntity.ok(response);
    }

    /**
     * Display the registration form.
     * 
     * @param model the model
     * @return the registration form view
     */
    @RequestMapping(value = "/add-registration", method = RequestMethod.GET)
    public String addRegistration(Model model) {
        LOGGER.debug("+addRegistration({})", model);
        Registration registration = new Registration();
        model.addAttribute("registration", registration);
        LOGGER.debug("-addRegistration(), model={}", model);
        return "addRegistration";
    }

    /**
     * Process the registration form submission.
     * 
     * @param registration the registration data
     * @param result the binding result
     * @param model the model
     * @return redirect to home page on success, or return to form on error
     */
    @RequestMapping(value = "/add-registration", method = RequestMethod.POST)
    public String processRegistration(@Valid @ModelAttribute("registration") Registration registration,
                                     BindingResult result,
                                     Model model) {
        LOGGER.debug("+processRegistration({}, {})", registration, result);

        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "addRegistration";
        }

        LOGGER.info("Registration submitted: FirstName={}, LastName={}, Email={}, Phone={}",
                registration.getFirstName(), registration.getLastName(),
                registration.getEmail(), registration.getPhoneNumber());

        // Registration data is logged - no database table for registrations
        // TODO: Add registrations table or integrate with users table if needed
        LOGGER.info("Registration received and logged: {}", registration);

        LOGGER.debug("-processRegistration(), redirecting to home page");
        return "redirect:/";
    }

    /**
     * REST API endpoint to create a registration.
     * Accepts JSON in request body.
     * 
     * @param registration the registration data
     * @return ResponseEntity with success or error message
     */
    @RequestMapping(value = "/api/registrations", method = RequestMethod.POST,
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRegistration(@Valid @RequestBody Registration registration) {
        LOGGER.debug("+createRegistration({})", registration);

        Map<String, Object> response = new HashMap<>();

        LOGGER.info("Registration API call: FirstName={}, LastName={}, Email={}, Phone={}",
                registration.getFirstName(), registration.getLastName(),
                registration.getEmail(), registration.getPhoneNumber());

        // Registration data is logged - no database table for registrations
        // TODO: Add registrations table or integrate with users table if needed
        LOGGER.info("Registration received and logged: {}", registration);

        response.put("success", true);
        response.put("message", "Registration received successfully (logged)");
        response.put("data", registration);

        LOGGER.debug("-createRegistration()");
        return ResponseEntity.ok(response);
    }
}

