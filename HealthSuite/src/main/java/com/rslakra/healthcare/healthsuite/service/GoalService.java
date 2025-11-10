package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.Goal;

import java.util.List;

/**
 * Service interface for goal operations.
 * 
 * @author rslakra
 */
public interface GoalService {

    /**
     * Save a goal.
     * 
     * @param goal the goal to save
     * @return the saved goal
     */
    Goal saveGoal(Goal goal);

    /**
     * Find a goal by ID.
     * 
     * @param id the goal ID
     * @return the goal, or null if not found
     */
    Goal findGoalById(Long id);

    /**
     * Find all goals for a given user ID.
     * 
     * @param userId the user ID
     * @return list of goals
     */
    List<Goal> findGoalsByUserId(Long userId);

    /**
     * Find all goals.
     * 
     * @return list of all goals
     */
    List<Goal> findAllGoals();

    /**
     * Update a goal.
     * 
     * @param goal the goal to update
     * @return true if successful
     */
    boolean updateGoal(Goal goal);

    /**
     * Delete a goal by ID.
     * 
     * @param id the goal ID
     * @return true if successful
     */
    boolean deleteGoal(Long id);
}

