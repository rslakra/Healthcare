package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of RoleRepository.
 * 
 * @author rslakra
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String FIND_BY_ID_SQL = 
        "SELECT id, name, description FROM roles WHERE id = ?";

    private static final String FIND_BY_NAME_SQL = 
        "SELECT id, name, description FROM roles WHERE name = ?";

    private static final String FIND_ALL_SQL = 
        "SELECT id, name, description FROM roles ORDER BY name";

    private static final String FIND_BY_USER_ID_SQL = 
        "SELECT r.id, r.name, r.description " +
        "FROM roles r " +
        "INNER JOIN user_roles ur ON r.id = ur.role_id " +
        "WHERE ur.user_id = ? " +
        "ORDER BY r.name";

    @Override
    public Role findById(Long id) {
        LOGGER.debug("Finding role by ID: {}", id);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                new RoleRowMapper(), id);
        } catch (Exception e) {
            LOGGER.debug("Role not found for ID: {}", id);
            return null;
        }
    }

    @Override
    public Role findByName(String name) {
        LOGGER.debug("Finding role by name: {}", name);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_NAME_SQL,
                new RoleRowMapper(), name);
        } catch (Exception e) {
            LOGGER.debug("Role not found for name: {}", name);
            return null;
        }
    }

    @Override
    public List<Role> findAll() {
        LOGGER.debug("Finding all roles");
        try {
            return jdbcTemplate.query(FIND_ALL_SQL, new RoleRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error finding all roles: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        LOGGER.debug("Finding roles by user ID: {}", userId);
        try {
            return jdbcTemplate.query(FIND_BY_USER_ID_SQL,
                new RoleRowMapper(), userId);
        } catch (Exception e) {
            LOGGER.error("Error finding roles by user ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private static class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getLong("id"));
            role.setName(rs.getString("name"));
            role.setDescription(rs.getString("description"));
            return role;
        }
    }
}

