package com.rslakra.healthcare.routinecheckup.service.impl;

import com.rslakra.healthcare.routinecheckup.entity.RoleEntity;
import com.rslakra.healthcare.routinecheckup.repository.RoleRepository;
import com.rslakra.healthcare.routinecheckup.service.RoleService;
import com.rslakra.healthcare.routinecheckup.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:19 PM
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleEntity> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        Optional<RoleEntity> result
            = roleRepository.findRoleEntityByRoleName(name);
        return result;
    }

    @Override
    @Transactional
    public RoleEntity createIfNotExists(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }

        return roleRepository.findRoleEntityByRoleName(roleName)
            .orElseGet(() -> {
                RoleEntity newRole = RoleEntity.builder()
                    .roleName(roleName)
                    .build();
                return roleRepository.save(newRole);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleEntity> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            UUID uuid = UUID.fromString(id);
            return roleRepository.findById(uuid);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public RoleEntity createRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        // Check if role already exists
        Optional<RoleEntity> existing = roleRepository.findRoleEntityByRoleName(roleName.trim().toUpperCase());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Role with name '" + roleName + "' already exists");
        }

        RoleEntity newRole = RoleEntity.builder()
            .roleName(roleName.trim().toUpperCase())
            .build();
        return roleRepository.save(newRole);
    }

    @Override
    @Transactional
    public RoleEntity updateRole(String id, String newRoleName) {
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        if (newRoleName == null || newRoleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        UUID uuid = UUID.fromString(id);
        RoleEntity role = roleRepository.findById(uuid)
            .orElseThrow(() -> new NotFoundException("Role with ID '" + id + "' not found"));

        // Check if new name already exists (excluding current role)
        Optional<RoleEntity> existing = roleRepository.findRoleEntityByRoleName(newRoleName.trim().toUpperCase());
        if (existing.isPresent() && !existing.get().getId().equals(uuid)) {
            throw new IllegalArgumentException("Role with name '" + newRoleName + "' already exists");
        }

        role.setRoleName(newRoleName.trim().toUpperCase());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }

        UUID uuid = UUID.fromString(id);
        RoleEntity role = roleRepository.findById(uuid)
            .orElseThrow(() -> new NotFoundException("Role with ID '" + id + "' not found"));

        // Prevent deletion of system roles (ADMIN, DOCTOR, NURSE, PATIENT)
        String roleName = role.getRoleName();
        if ("ADMIN".equals(roleName) || "DOCTOR".equals(roleName) || 
            "NURSE".equals(roleName) || "PATIENT".equals(roleName)) {
            throw new IllegalArgumentException("Cannot delete system role '" + roleName + "'");
        }

        roleRepository.delete(role);
    }
}
