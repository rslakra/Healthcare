package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Role;

import java.util.List;

/**
 * Repository interface for role operations.
 * 
 * @author rslakra
 */
public interface RoleRepository {

    /**
     * Find a role by its ID.
     * 
     * @param id the role ID
     * @return the Role if found, null otherwise
     */
    Role findById(Long id);

    /**
     * Find a role by its name.
     * 
     * @param name the role name
     * @return the Role if found, null otherwise
     */
    Role findByName(String name);

    /**
     * Find all roles.
     * 
     * @return a list of all Role
     */
    List<Role> findAll();

    /**
     * Find roles for a specific user.
     * 
     * @param userId the user ID
     * @return a list of Role for the user
     */
    List<Role> findByUserId(Long userId);
}

