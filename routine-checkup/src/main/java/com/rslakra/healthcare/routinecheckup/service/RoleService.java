package com.rslakra.healthcare.routinecheckup.service;


import com.rslakra.healthcare.routinecheckup.entity.RoleEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:15 PM
 */
public interface RoleService {

    Optional<RoleEntity> findByName(String name);

    /**
     * Creates a role if it doesn't exist
     * @param roleName The name of the role to create
     * @return The created or existing role entity
     */
    RoleEntity createIfNotExists(String roleName);

    /**
     * Get all roles
     * @return List of all roles
     */
    List<RoleEntity> getAllRoles();

    /**
     * Get role by ID
     * @param id Role ID
     * @return Role entity
     */
    Optional<RoleEntity> findById(String id);

    /**
     * Create a new role
     * @param roleName The name of the role to create
     * @return The created role entity
     */
    RoleEntity createRole(String roleName);

    /**
     * Update a role
     * @param id Role ID
     * @param newRoleName New role name
     * @return Updated role entity
     */
    RoleEntity updateRole(String id, String newRoleName);

    /**
     * Delete a role
     * @param id Role ID
     */
    void deleteRole(String id);

}
