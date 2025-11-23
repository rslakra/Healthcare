package com.rslakra.healthcare.routinecheckup.controller;

import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.service.security.TokenService;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin/Utility controller for development and testing purposes
 * 
 * @author Rohtash Lakra
 * @created 11/23/25
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final TokenService tokenService;

    /**
     * Manually activate a user account by username (for development/testing)
     * Usage: /admin/activate/{username}
     */
    @GetMapping("/activate/{username}")
    public String activateUser(@PathVariable String username, Model model) {
        try {
            var userEntity = userService.getUserEntityByLogin(username);
            if (userEntity == null) {
                model.addAttribute("message", "User '" + username + "' not found!");
                model.addAttribute("success", false);
                return "admin-result";
            }

            if (!userEntity.getIsTemporary()) {
                model.addAttribute("message", "User '" + username + "' is already activated!");
                model.addAttribute("success", true);
                return "admin-result";
            }

            // Generate registration token and complete registration
            String token = tokenService.generateRegistrationToken(userEntity);
            userService.completeRegistration(token);
            
            log.info("User '{}' manually activated by admin endpoint", username);
            model.addAttribute("message", "User '" + username + "' has been successfully activated! You can now login.");
            model.addAttribute("success", true);
            model.addAttribute("loginUrl", ViewNames.LOGIN_URL);
            
        } catch (Exception e) {
            log.error("Error activating user '{}': {}", username, e.getMessage(), e);
            model.addAttribute("message", "Error activating user: " + e.getMessage());
            model.addAttribute("success", false);
        }
        
        return "admin-result";
    }

    /**
     * Generate registration token for a user (for development/testing)
     * Usage: /admin/token/{username}
     */
    @GetMapping("/token/{username}")
    public String generateToken(@PathVariable String username, Model model) {
        try {
            var userEntity = userService.getUserEntityByLogin(username);
            if (userEntity == null) {
                model.addAttribute("message", "User '" + username + "' not found!");
                model.addAttribute("success", false);
                return "admin-result";
            }

            String token = tokenService.generateRegistrationToken(userEntity);
            String registrationUrl = "/routine-checkup" + ViewNames.REGISTRATION_URL + "/" + token;
            
            log.info("Registration token generated for user '{}'", username);
            model.addAttribute("message", "Registration token generated for user '" + username + "'");
            model.addAttribute("token", token);
            model.addAttribute("registrationUrl", registrationUrl);
            model.addAttribute("success", true);
            
        } catch (Exception e) {
            log.error("Error generating token for user '{}': {}", username, e.getMessage(), e);
            model.addAttribute("message", "Error generating token: " + e.getMessage());
            model.addAttribute("success", false);
        }
        
        return "admin-result";
    }
}
