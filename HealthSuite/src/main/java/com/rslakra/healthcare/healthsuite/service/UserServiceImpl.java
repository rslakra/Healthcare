package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.User;
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

    @Override
    public boolean saveUser(User user) {
        LOGGER.debug("Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        LOGGER.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        LOGGER.debug("Finding user by ID: {}", id);
        // Note: This method may need to be added to UserRepository if not present
        // For now, we'll use findByUsername after getting username from id
        // This is a placeholder - you may want to add findById to repository
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        LOGGER.debug("Checking if username exists: {}", username);
        return userRepository.existsByUsername(username);
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
}

