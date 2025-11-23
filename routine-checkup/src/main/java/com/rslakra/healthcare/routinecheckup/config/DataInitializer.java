package com.rslakra.healthcare.routinecheckup.config;

import com.rslakra.healthcare.routinecheckup.service.RoleService;
import com.rslakra.healthcare.routinecheckup.utils.security.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Data Initializer - Ensures default roles exist in the database
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Run early in the startup process
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;

    @Override
    public void run(String... args) {
        log.info("Initializing default roles...");
        
        int createdCount = 0;
        int existingCount = 0;
        
        // Initialize all healthcare roles
        for (Roles role : Roles.values()) {
            try {
                // Check if role already exists before creating
                boolean roleExists = roleService.findByName(role.getValue()).isPresent();
                roleService.createIfNotExists(role.getValue());
                
                if (roleExists) {
                    existingCount++;
                    log.debug("Role '{}' already exists, skipping creation", role.getValue());
                } else {
                    createdCount++;
                    log.info("Role '{}' created successfully", role.getValue());
                }
            } catch (Exception e) {
                log.error("Failed to initialize role '{}': {}", role.getValue(), e.getMessage());
            }
        }
        
        log.info("Default roles initialization completed - Created: {}, Already existed: {}", createdCount, existingCount);
    }
}

