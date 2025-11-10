package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Goal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JDBC implementation of GoalRepository.
 * 
 * @author rslakra
 */
@Repository
public class GoalRepositoryImpl implements GoalRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoalRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GoalRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = 
        "INSERT INTO goals (user_id, activity_type, minutes) " +
        "VALUES (?, ?, ?)";

    private static final String FIND_BY_ID_SQL = 
        "SELECT id, user_id, activity_type, minutes, created_at, updated_at " +
        "FROM goals WHERE id = ?";

    private static final String FIND_BY_USER_ID_SQL = 
        "SELECT id, user_id, activity_type, minutes, created_at, updated_at " +
        "FROM goals WHERE user_id = ? ORDER BY created_at DESC";

    private static final String FIND_ALL_SQL = 
        "SELECT id, user_id, activity_type, minutes, created_at, updated_at " +
        "FROM goals ORDER BY created_at DESC";

    private static final String UPDATE_SQL = 
        "UPDATE goals SET user_id = ?, activity_type = ?, minutes = ? " +
        "WHERE id = ?";

    private static final String DELETE_BY_ID_SQL = 
        "DELETE FROM goals WHERE id = ?";

    @Override
    public Goal save(Goal goal) {
        LOGGER.debug("Saving goal: {}", goal);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL, 
                    Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, goal.getUserId());
                ps.setString(2, goal.getActivityType());
                ps.setInt(3, goal.getMinutes());
                return ps;
            }, keyHolder);

            Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
            goal.setId(generatedId);
            goal.setCreatedAt(LocalDateTime.now());
            LOGGER.debug("Goal saved with ID: {}", generatedId);
            return goal;
        } catch (Exception e) {
            LOGGER.error("Error saving goal: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save goal", e);
        }
    }

    @Override
    public Goal findById(Long id) {
        LOGGER.debug("Finding goal by ID: {}", id);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                new GoalRowMapper(), id);
        } catch (Exception e) {
            LOGGER.debug("Goal not found for ID: {}", id);
            return null;
        }
    }

    @Override
    public List<Goal> findByUserId(Long userId) {
        LOGGER.debug("Finding goals by user ID: {}", userId);
        try {
            return jdbcTemplate.query(FIND_BY_USER_ID_SQL,
                new GoalRowMapper(), userId);
        } catch (Exception e) {
            LOGGER.error("Error finding goals by user ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Goal> findAll() {
        LOGGER.debug("Finding all goals");
        try {
            return jdbcTemplate.query(FIND_ALL_SQL, new GoalRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error finding all goals: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean update(Goal goal) {
        LOGGER.debug("Updating goal: {}", goal.getId());
        try {
            int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
                goal.getUserId(),
                goal.getActivityType(),
                goal.getMinutes(),
                goal.getId());
            LOGGER.debug("Goal updated, rows affected: {}", rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Error updating goal: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        LOGGER.debug("Deleting goal by ID: {}", id);
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_BY_ID_SQL, id);
            LOGGER.debug("Goal deleted, rows affected: {}", rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Error deleting goal: {}", e.getMessage(), e);
            return false;
        }
    }

    private static class GoalRowMapper implements RowMapper<Goal> {
        @Override
        public Goal mapRow(ResultSet rs, int rowNum) throws SQLException {
            Goal goal = new Goal();
            goal.setId(rs.getLong("id"));
            goal.setUserId(rs.getLong("user_id"));
            goal.setActivityType(rs.getString("activity_type"));
            goal.setMinutes(rs.getInt("minutes"));
            
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                goal.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                goal.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            
            return goal;
        }
    }
}

