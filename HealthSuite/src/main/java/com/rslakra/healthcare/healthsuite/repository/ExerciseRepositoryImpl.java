package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Exercise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * JDBC implementation of ExerciseRepository.
 * 
 * @author rslakra
 */
@Repository
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExerciseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = 
        "INSERT INTO exercises (user_id, activity_type, minutes, description, exercise_date) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = 
        "UPDATE exercises SET user_id = ?, activity_type = ?, minutes = ?, " +
        "description = ?, exercise_date = ? WHERE id = ?";

    private static final String FIND_BY_ID_SQL = 
        "SELECT id, user_id, activity_type, minutes, description, exercise_date, created_at, updated_at " +
        "FROM exercises WHERE id = ?";

    private static final String FIND_BY_USER_ID_SQL = 
        "SELECT id, user_id, activity_type, minutes, description, exercise_date, created_at, updated_at " +
        "FROM exercises WHERE user_id = ? ORDER BY exercise_date DESC, created_at DESC";

    private static final String FIND_BY_USER_ID_AND_DATE_RANGE_SQL = 
        "SELECT id, user_id, activity_type, minutes, description, exercise_date, created_at, updated_at " +
        "FROM exercises WHERE user_id = ? AND exercise_date BETWEEN ? AND ? " +
        "ORDER BY exercise_date DESC, created_at DESC";

    private static final String FIND_ALL_SQL = 
        "SELECT id, user_id, activity_type, minutes, description, exercise_date, created_at, updated_at " +
        "FROM exercises ORDER BY exercise_date DESC, created_at DESC";

    private static final String DELETE_BY_ID_SQL = 
        "DELETE FROM exercises WHERE id = ?";

    @Override
    public Exercise save(Exercise exercise) {
        LOGGER.debug("Saving exercise: {}", exercise);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL, 
                    Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, exercise.getUserId());
                ps.setString(2, exercise.getActivity());
                ps.setInt(3, exercise.getMinutes());
                ps.setString(4, exercise.getDescription());
                ps.setDate(5, Date.valueOf(exercise.getExerciseDate()));
                return ps;
            }, keyHolder);

            Long generatedId = null;
            if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
                Object id = keyHolder.getKeys().get("ID");
                if (id == null) {
                    // Try lowercase as fallback
                    id = keyHolder.getKeys().get("id");
                }
                if (id != null) {
                    generatedId = ((Number) id).longValue();
                }
            }
            exercise.setId(generatedId);
            LOGGER.debug("Exercise saved with ID: {}", generatedId);
            return exercise;
        } catch (Exception e) {
            LOGGER.error("Error saving exercise: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save exercise", e);
        }
    }

    @Override
    public Exercise findById(Long id) {
        LOGGER.debug("Finding exercise by ID: {}", id);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_SQL,
                new ExerciseRowMapper(), id);
        } catch (Exception e) {
            LOGGER.debug("Exercise not found for ID: {}", id);
            return null;
        }
    }

    @Override
    public List<Exercise> findByUserId(Long userId) {
        LOGGER.debug("Finding exercises by user ID: {}", userId);
        try {
            return jdbcTemplate.query(FIND_BY_USER_ID_SQL,
                new ExerciseRowMapper(), userId);
        } catch (Exception e) {
            LOGGER.error("Error finding exercises by user ID: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Exercise> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("Finding exercises by user ID {} and date range {} to {}", userId, startDate, endDate);
        try {
            return jdbcTemplate.query(FIND_BY_USER_ID_AND_DATE_RANGE_SQL,
                new ExerciseRowMapper(), userId, Date.valueOf(startDate), Date.valueOf(endDate));
        } catch (Exception e) {
            LOGGER.error("Error finding exercises by user ID and date range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Exercise> findAll() {
        LOGGER.debug("Finding all exercises");
        try {
            return jdbcTemplate.query(FIND_ALL_SQL, new ExerciseRowMapper());
        } catch (Exception e) {
            LOGGER.error("Error finding all exercises: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean update(Exercise exercise) {
        LOGGER.debug("Updating exercise: {}", exercise.getId());
        try {
            int rowsAffected = jdbcTemplate.update(UPDATE_SQL,
                exercise.getUserId(),
                exercise.getActivity(),
                exercise.getMinutes(),
                exercise.getDescription(),
                Date.valueOf(exercise.getExerciseDate()),
                exercise.getId());
            LOGGER.debug("Exercise updated, rows affected: {}", rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Error updating exercise: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Long id) {
        LOGGER.debug("Deleting exercise by ID: {}", id);
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_BY_ID_SQL, id);
            LOGGER.debug("Exercise deleted, rows affected: {}", rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Error deleting exercise: {}", e.getMessage(), e);
            return false;
        }
    }

    private static class ExerciseRowMapper implements RowMapper<Exercise> {
        @Override
        public Exercise mapRow(ResultSet rs, int rowNum) throws SQLException {
            Exercise exercise = new Exercise();
            exercise.setId(rs.getLong("id"));
            exercise.setUserId(rs.getLong("user_id"));
            exercise.setActivity(rs.getString("activity_type"));
            exercise.setMinutes(rs.getInt("minutes"));
            exercise.setDescription(rs.getString("description"));
            
            Date exerciseDate = rs.getDate("exercise_date");
            if (exerciseDate != null) {
                exercise.setExerciseDate(exerciseDate.toLocalDate());
            }
            
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                exercise.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                exercise.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            
            return exercise;
        }
    }
}

