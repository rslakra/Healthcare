package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * JDBC implementation of UserRepository.
 * 
 * @author rslakra
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String UPDATE_SQL = 
        "UPDATE users SET email = ?, first_name = ?, last_name = ?, enabled = ? " +
        "WHERE username = ?";

    private static final String INSERT_SQL = 
        "INSERT INTO users (username, email, first_name, last_name, password, enabled) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_USERNAME_SQL = 
        "SELECT id, username, email, first_name, last_name, password, enabled, created_at, updated_at " +
        "FROM users WHERE username = ?";

    private static final String FIND_BY_ID_SQL = 
        "SELECT id, username, email, first_name, last_name, password, enabled, created_at, updated_at " +
        "FROM users WHERE id = ?";

    private static final String EXISTS_BY_USERNAME_SQL = 
        "SELECT COUNT(*) FROM users WHERE username = ?";

    private static final String FIND_ALL_SQL = 
        "SELECT id, username, email, first_name, last_name, password, enabled, created_at, updated_at " +
        "FROM users ORDER BY username";

    @Override
    public boolean save(User user) {
        LOGGER.debug("Saving user: {}", user.getUsername());
        try {
            boolean exists = existsByUsername(user.getUsername());
            int rowsAffected;
            
            if (exists) {
                // Update existing user profile fields
                rowsAffected = jdbcTemplate.update(UPDATE_SQL,
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEnabled() != null ? user.getEnabled() : true,
                    user.getUsername());
                LOGGER.debug("User updated, rows affected: {}", rowsAffected);
            } else {
                // Insert new user
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    LOGGER.warn("Cannot create user - password is required for new users");
                    return false;
                }
                rowsAffected = jdbcTemplate.update(INSERT_SQL,
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPassword(),
                    user.getEnabled() != null ? user.getEnabled() : true);
                LOGGER.debug("User created, rows affected: {}", rowsAffected);
            }
            
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Error saving user: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        LOGGER.debug("Finding user by username: {}", username);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_USERNAME_SQL,
                new UserRowMapper(), username);
        } catch (Exception e) {
            LOGGER.debug("User not found for username: {}", username);
            return null;
        }
    }

    @Override
    public User findById(Long id) {
        LOGGER.debug("Finding user by ID: {}", id);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                new UserRowMapper(), id);
        } catch (Exception e) {
            LOGGER.debug("User not found for ID: {}", id);
            return null;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        LOGGER.debug("Checking if username exists: {}", username);
        try {
            Integer count = jdbcTemplate.queryForObject(EXISTS_BY_USERNAME_SQL,
                Integer.class, username);
            return count != null && count > 0;
        } catch (Exception e) {
            LOGGER.error("Error checking username existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public java.util.List<User> findAll() {
        LOGGER.debug("Finding all users");
        try {
            return jdbcTemplate.query(FIND_ALL_SQL, new UserRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error finding all users: {}", e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                user.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            
            return user;
        }
    }
}

