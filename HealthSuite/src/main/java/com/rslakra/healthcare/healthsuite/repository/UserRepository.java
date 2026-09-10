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
     * Find a user by email.
     *
     * @param email the email address
     * @return the user, or null if not found
     */
    User findByEmail(String email);

    /**
     * Check if a username exists in users table.
     * 
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email exists in users table.
     *
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Update a user's password.
     *
     * @param username the username
     * @param password the new password
     * @return true if successful, false otherwise
     */
    boolean updatePassword(String username, String password);

    /**
     * Find all users.
     * 
     * @return list of all users
     */
    java.util.List<User> findAll();

    /**
     * Find a user by ID.
     * 
     * @param id the user ID
     * @return the user, or null if not found
     */
    User findById(Long id);
}

