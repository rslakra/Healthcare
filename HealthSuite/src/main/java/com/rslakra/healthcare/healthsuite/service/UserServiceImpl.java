package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.User;
import com.rslakra.healthcare.healthsuite.repository.RoleRepository;
import com.rslakra.healthcare.healthsuite.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for user operations.
 * 
 * @author rslakra
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean saveUser(User user) {
        LOGGER.debug("Saving user: {}", user.getUsername());
        boolean isNewUser = !userRepository.existsByUsername(user.getUsername());
        if (!userRepository.save(user)) {
            return false;
        }

        if (isNewUser) {
            User savedUser = userRepository.findByUsername(user.getUsername());
            if (savedUser != null) {
                roleRepository.assignRoleToUser(savedUser.getId(), "USER");
            }
        }
        return true;
    }

    @Override
    public User findByUsername(String username) {
        LOGGER.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        LOGGER.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        LOGGER.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        LOGGER.debug("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean registerUser(User user) {
        LOGGER.debug("Registering user: {}", user.getUsername());
        user.setEnabled(true);
        if (!userRepository.save(user)) {
            return false;
        }

        User savedUser = userRepository.findByUsername(user.getUsername());
        if (savedUser == null) {
            LOGGER.error("User saved but could not be loaded: {}", user.getUsername());
            return false;
        }

        return roleRepository.assignRoleToUser(savedUser.getId(), "USER");
    }

    @Override
    public boolean resetPassword(String username, String newPassword) {
        LOGGER.debug("Resetting password for user: {}", username);
        if (!userRepository.existsByUsername(username)) {
            return false;
        }
        return userRepository.updatePassword(username, newPassword);
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @Override
    public java.util.List<User> findAllUsers() {
        LOGGER.debug("Finding all users");
        return userRepository.findAll();
    }
}

