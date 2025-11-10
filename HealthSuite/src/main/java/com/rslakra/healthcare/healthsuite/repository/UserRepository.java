package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.User;

/**
 * Repository interface for user operations.
 * 
 * @author rslakra
 */
public interface UserRepository {

    /**
     * Save or update a user profile.
     * 
     * @param user the user to save
     * @return true if successful, false otherwise
     */
    boolean save(User user);

    /**
     * Find a user by username.
     * 
     * @param username the username
     * @return the user, or null if not found
     */
    User findByUsername(String username);

    /**
     * Check if a username exists in users table.
     * 
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);
}

