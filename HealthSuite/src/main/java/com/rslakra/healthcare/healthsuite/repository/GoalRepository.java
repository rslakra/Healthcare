package com.rslakra.healthcare.healthsuite.repository;

import com.rslakra.healthcare.healthsuite.model.Goal;

import java.util.List;

/**
 * Repository interface for goal operations.
 * 
 * @author rslakra
 */
public interface GoalRepository {

    /**
     * Save a new goal.
     * 
     * @param goal the goal to save
     * @return the saved goal with its generated ID
     */
    Goal save(Goal goal);

    /**
     * Find a goal by its ID.
     * 
     * @param id the ID of the goal
     * @return the Goal if found, null otherwise
     */
    Goal findById(Long id);

    /**
     * Find all goals for a given user ID.
     * 
     * @param userId the user ID
     * @return a list of Goal
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Find all goals.
     * 
     * @return a list of all Goal
     */
    List<Goal> findAll();

    /**
     * Update an existing goal.
     * 
     * @param goal the goal to update
     * @return true if the update was successful, false otherwise
     */
    boolean update(Goal goal);

    /**
     * Delete a goal by its ID.
     * 
     * @param id the ID of the goal to delete
     * @return true if the deletion was successful, false otherwise
     */
    boolean deleteById(Long id);
}

