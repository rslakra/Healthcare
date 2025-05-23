package com.rslakra.healthcare.routinecheckup.config;

import com.rslakra.healthcare.routinecheckup.utils.security.RoleNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 3:56 PM
 */
@Configuration
public class SecurityBeansConfig {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 4;
    private static final int MEMORY = 1024 * 512;
    private static final int ITERATIONS = 16;

    @Bean
    public PasswordEncoder passwordEncoder() {
        Argon2PasswordEncoder encoder
                = new Argon2PasswordEncoder(
                SALT_LENGTH,
                HASH_LENGTH,
                PARALLELISM,
                MEMORY,
                ITERATIONS
        );
        return encoder;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();

        String hierarchyRepresentation
                = String.format(
                "ROLE_%s > ROLE_%s",
                RoleNames.ADMIN.getValue(),
                RoleNames.USER.getValue()
        );
        hierarchy.setHierarchy(hierarchyRepresentation);

        return hierarchy;
    }

}
