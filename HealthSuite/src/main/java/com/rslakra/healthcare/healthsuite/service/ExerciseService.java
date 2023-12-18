package com.rslakra.healthcare.healthsuite.service;

import com.rslakra.healthcare.healthsuite.model.Activity;

import java.util.List;

/**
 * @author rlakra
 */
public interface ExerciseService {

    /**
     * @return
     */
    List<Activity> findAllActivities();

}
