package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.Goal;
import com.rslakra.healthcare.healthsuite.repository.GoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for goal operations.
 * 
 * @author rslakra
 */
@Service("goalService")
public class GoalServiceImpl implements GoalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoalServiceImpl.class);

    @Autowired
    private GoalRepository goalRepository;

    @Override
    public Goal saveGoal(Goal goal) {
        LOGGER.debug("Saving goal: {}", goal);
        return goalRepository.save(goal);
    }

    @Override
    public Goal findGoalById(Long id) {
        LOGGER.debug("Finding goal by ID: {}", id);
        return goalRepository.findById(id);
    }

    @Override
    public List<Goal> findGoalsByUserId(Long userId) {
        LOGGER.debug("Finding goals by user ID: {}", userId);
        return goalRepository.findByUserId(userId);
    }

    @Override
    public List<Goal> findAllGoals() {
        LOGGER.debug("Finding all goals");
        return goalRepository.findAll();
    }

    @Override
    public boolean updateGoal(Goal goal) {
        LOGGER.debug("Updating goal: {}", goal.getId());
        return goalRepository.update(goal);
    }

    @Override
    public boolean deleteGoal(Long id) {
        LOGGER.debug("Deleting goal by ID: {}", id);
        return goalRepository.deleteById(id);
    }
}

