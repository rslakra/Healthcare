package com.rslakra.healthcare.healthsuite.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
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
     * Display the add user form (for new users).
     * 
     * @param model the model
     * @return the edit user form view
     */
    @RequestMapping(value = "/add-users", method = RequestMethod.GET)
    public String addUser(Model model) {
        LOGGER.debug("+addUser({})", model);
        User user = new User();
        model.addAttribute("user", user);
        LOGGER.debug("-addUser(), model={}", model);
        return "editUser";
    }

    /**
     * Display the edit user form (for existing users).
     * 
     * @param id the user ID
     * @param model the model
     * @return the edit user form view or redirect if user not found
     */
    @RequestMapping(value = "/users/{id}/edit", method = RequestMethod.GET)
    public String editUser(@PathVariable Long id, Model model) {
        LOGGER.debug("+editUser({}, {})", id, model);
        User user = userService.findById(id);
        if (user == null) {
            LOGGER.warn("User not found with ID: {}", id);
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        LOGGER.debug("-editUser(), model={}", model);
        return "editUser";
    }

    /**
     * Process the user form submission (for both new and existing users).
     * 
     * @param user the user data
     * @param result the binding result
     * @param model the model
     * @return redirect to users list on success, or return to form on error
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String saveUser(@Valid @ModelAttribute("user") User user,
                          BindingResult result,
                          Model model) {
        LOGGER.debug("+saveUser({}, {})", user, result);

        boolean isNewUser = (user.getId() == null);
        
        // For new users, password is required
        if (isNewUser && (user.getPassword() == null || user.getPassword().trim().isEmpty())) {
            result.rejectValue("password", "NotBlank", "Password is required for new users");
        }

        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "editUser";
        }

        LOGGER.info("User {}: Username={}, Email={}, FirstName={}, LastName={}",
                isNewUser ? "submitted" : "updated",
                user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName());

        try {
            boolean saved = userService.saveUser(user);
            if (!saved) {
                LOGGER.error("Failed to save user");
                model.addAttribute("error", "Failed to save user. Please try again.");
                return "editUser";
            }
            LOGGER.info("User {} successfully", isNewUser ? "created" : "updated");
        } catch (Exception e) {
            LOGGER.error("Error saving user: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while saving the user.");
            return "editUser";
        }

        LOGGER.debug("-saveUser(), redirecting to users list");
        return "redirect:/users";
    }

    /**
     * Process the update user form submission (for existing users).
     * 
     * @param id the user ID
     * @param user the user data
     * @param result the binding result
     * @param model the model
     * @return redirect to users list on success, or return to form on error
     */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.POST)
    public String updateUser(@PathVariable Long id,
                            @Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            Model model) {
        LOGGER.debug("+updateUser({}, {})", id, user);

        user.setId(id);
        
        // For existing users, password field is not shown in form, so preserve existing password
        // Load existing user to preserve password if not provided
        User existingUser = userService.findById(id);
        if (existingUser != null) {
            // If password is null or empty, use existing password
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
        }
        
        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "editUser";
        }

        LOGGER.info("User updated: ID={}, Username={}, Email={}, FirstName={}, LastName={}",
                id, user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName());

        try {
            boolean saved = userService.saveUser(user);
            if (!saved) {
                LOGGER.error("Failed to update user");
                model.addAttribute("error", "Failed to update user. Please try again.");
                return "editUser";
            }
            LOGGER.info("User updated successfully");
        } catch (Exception e) {
            LOGGER.error("Error updating user: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while updating the user.");
            return "editUser";
        }

        LOGGER.debug("-updateUser(), redirecting to users list");
        return "redirect:/users";
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
     * Display list of all users.
     * 
     * @param model the model
     * @return the list users view
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String listUsers(Model model) {
        LOGGER.debug("+listUsers({})", model);
        java.util.List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        LOGGER.debug("-listUsers(), found {} users", users.size());
        return "listUsers";
    }
}

