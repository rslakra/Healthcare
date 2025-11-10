package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Exercise;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for exercise operations.
 * 
 * @author rslakra
 */
public interface ExerciseRepository {

    /**
     * Save an exercise.
     * 
     * @param exercise the exercise to save
     * @return the saved exercise with generated ID
     */
    Exercise save(Exercise exercise);

    /**
     * Find an exercise by ID.
     * 
     * @param id the exercise ID
     * @return the exercise, or null if not found
     */
    Exercise findById(Long id);

    /**
     * Find all exercises by user ID.
     * 
     * @param userId the user ID
     * @return list of exercises
     */
    List<Exercise> findByUserId(Long userId);

    /**
     * Find exercises by user ID and date range.
     * 
     * @param userId the user ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of exercises
     */
    List<Exercise> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find all exercises.
     * 
     * @return list of all exercises
     */
    List<Exercise> findAll();

    /**
     * Update an existing exercise.
     * 
     * @param exercise the exercise to update
     * @return true if successful, false otherwise
     */
    boolean update(Exercise exercise);

    /**
     * Delete an exercise by ID.
     * 
     * @param id the exercise ID
     * @return true if successful, false otherwise
     */
    boolean deleteById(Long id);
}

