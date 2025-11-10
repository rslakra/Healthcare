package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.User;

/**
 * Service interface for user operations.
 * 
 * @author rslakra
 */
public interface UserService {

    /**
     * Save or update a user profile.
     * 
     * @param user the user to save
     * @return true if successful, false otherwise
     */
    boolean saveUser(User user);

    /**
     * Find a user by username.
     * 
     * @param username the username
     * @return the user, or null if not found
     */
    User findByUsername(String username);

    /**
     * Find a user by ID.
     * 
     * @param id the user ID
     * @return the user, or null if not found
     */
    User findById(Long id);

    /**
     * Check if a username exists.
     * 
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Get the current authenticated user's ID.
     * 
     * @return the user ID, or null if not authenticated
     */
    Long getCurrentUserId();

    /**
     * Find all users.
     * 
     * @return list of all users
     */
    java.util.List<User> findAllUsers();
}

