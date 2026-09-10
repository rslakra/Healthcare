package com.rslakra.healthcare.healthsuite.security;

import com.rslakra.healthcare.healthsuite.model.Role;
import com.rslakra.healthcare.healthsuite.model.User;
import com.rslakra.healthcare.healthsuite.repository.RoleRepository;
import com.rslakra.healthcare.healthsuite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads users by username or email for Spring Security authentication.
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public SecurityUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getId() != null) {
            for (Role role : roleRepository.findByUserId(user.getId())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        boolean enabled = user.getEnabled() == null || user.getEnabled();

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .disabled(!enabled)
            .authorities(authorities)
            .build();
    }
}
