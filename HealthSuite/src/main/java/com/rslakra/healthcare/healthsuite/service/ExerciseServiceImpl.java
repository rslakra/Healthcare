package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.Activity;
import com.rslakra.healthcare.healthsuite.model.ActivityType;
import com.rslakra.healthcare.healthsuite.model.Exercise;
import com.rslakra.healthcare.healthsuite.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for exercise operations.
 * 
 * @author rslakra
 */
@Service("exerciseService")
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public List<Activity> findAllActivities() {
        List<Activity> activities = new ArrayList<>();
        
        // Convert ActivityType enum to Activity list
        for (ActivityType type : ActivityType.values()) {
            Activity activity = new Activity();
            activity.setDesc(type.getCode());
            activities.add(activity);
        }
        
        return activities;
    }

    @Override
    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise findExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> findExercisesByUserId(Long userId) {
        return exerciseRepository.findByUserId(userId);
    }

    @Override
    public List<Exercise> findExercisesByUserIdAndDateRange(Long userId, 
                                                                   LocalDate startDate, 
                                                                   LocalDate endDate) {
        return exerciseRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<Exercise> findAllExercises() {
        return exerciseRepository.findAll();
    }

    @Override
    public boolean updateExercise(Exercise exercise) {
        return exerciseRepository.update(exercise);
    }

    @Override
    public boolean deleteExercise(Long id) {
        return exerciseRepository.deleteById(id);
    }
}
