package com.rslakra.healthcare.healthsuite.controller;

import com.rslakra.healthcare.healthsuite.model.Activity;
import com.rslakra.healthcare.healthsuite.model.Exercise;
import com.rslakra.healthcare.healthsuite.model.Goal;
import com.rslakra.healthcare.healthsuite.service.ExerciseService;
import com.rslakra.healthcare.healthsuite.service.GoalService;
import com.rslakra.healthcare.healthsuite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling activity operations, goals, and minutes tracking.
 * 
 * @author rslakra
 */
@Controller
@SessionAttributes("goal")
public class ActivityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    /**
     * Add authentication info to model for all requests.
     * 
     * @param model the model
     */
    @ModelAttribute
    public void addAuthenticationInfo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
    }

    /**
     * Display the add goal form.
     * 
     * @param model the model
     * @return the add goal form view
     */
    @RequestMapping(value = "/add-goal", method = RequestMethod.GET)
    public String addGoal(Model model) {
        LOGGER.debug("+addGoal({})", model);
        Goal goal = new Goal();
        goal.setMinutes(10);
        Long userId = userService.getCurrentUserId();
        if (userId != null) {
            goal.setUserId(userId);
        }
        model.addAttribute("goal", goal);
        LOGGER.debug("-addGoal(), model={}", model);
        return "addGoal";
    }

    /**
     * Process the add goal form submission.
     * 
     * @param goal the goal data
     * @param result the binding result
     * @return redirect to home page on success, or return to form on error
     */
    @RequestMapping(value = "/add-goal", method = RequestMethod.POST)
    public String updateGoal(@Valid @ModelAttribute("goal") Goal goal, BindingResult result) {
        LOGGER.debug("+updateGoal({}, {})", goal, result);

        LOGGER.debug("result={}, hasErrors={}, goal.getMinutes={}", result, result.hasErrors(), goal.getMinutes());
        if (result.hasErrors()) {
            return "addGoal";
        }

        try {
            // Set userId if not already set
            if (goal.getUserId() == null) {
                Long userId = userService.getCurrentUserId();
                if (userId != null) {
                    goal.setUserId(userId);
                }
            }
            
            Goal saved = goalService.saveGoal(goal);
            LOGGER.info("Goal saved successfully with ID: {}", saved.getId());
        } catch (Exception e) {
            LOGGER.error("Error saving goal: {}", e.getMessage(), e);
        }

        LOGGER.debug("-updateGoal(), redirect to index page.");
        return "redirect:/";
    }

    /**
     * Display the add minutes form.
     * 
     * @param exercise the exercise model
     * @param model the model
     * @param goal the goal from session
     * @return the add minutes form view
     */
    @RequestMapping(value = "/add-minutes", method = RequestMethod.GET)
    public String getMinutes(@ModelAttribute("exercise") Exercise exercise,
                             Model model,
                             @SessionAttribute(value = "goal", required = false) Goal goal) {
        if (goal != null) {
            model.addAttribute("goal", goal);
        }
        return "addMinutes";
    }

    /**
     * Process the add minutes form submission.
     * 
     * @param exercise the exercise data
     * @param result the binding result
     * @param model the model
     * @param goal the goal from session
     * @return the add minutes view
     */
    @RequestMapping(value = "/add-minutes", method = RequestMethod.POST)
    public String addMinutes(@Valid @ModelAttribute("exercise") Exercise exercise,
                             BindingResult result,
                             Model model,
                             @SessionAttribute(value = "goal", required = false) Goal goal) {

        LOGGER.debug("exercise: {}", exercise.getMinutes());
        LOGGER.debug("exercise activity: {}", exercise.getActivity());

        if (goal != null) {
            model.addAttribute("goal", goal);
        }

        if (result.hasErrors()) {
            return "addMinutes";
        }

        return "addMinutes";
    }

    /**
     * Returns list of all activities as JSON.
     * Supports both /activities and /activities.json endpoints.
     * 
     * @return List of Activity objects as JSON
     */
    @RequestMapping(value = {"/activities", "/activities.json"}, 
                   method = RequestMethod.GET,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Activity> findAllActivities() {
        return exerciseService.findAllActivities();
    }

    /**
     * Display the add exercise form.
     * 
     * @param model the model
     * @return the add exercise form view
     */
    @RequestMapping(value = "/add-exercise", method = RequestMethod.GET)
    public String addExercise(Model model) {
        LOGGER.debug("+addExercise({})", model);
        Exercise exercise = new Exercise();
        exercise.setExerciseDate(LocalDate.now());
        Long userId = userService.getCurrentUserId();
        if (userId != null) {
            exercise.setUserId(userId);
        }
        model.addAttribute("exercise", exercise);
        model.addAttribute("activityTypes", com.rslakra.healthcare.healthsuite.model.ActivityType.values());
        LOGGER.debug("-addExercise(), model={}", model);
        return "addExercise";
    }

    /**
     * Process the add exercise form submission.
     * 
     * @param exercise the exercise data
     * @param result the binding result
     * @param model the model
     * @return redirect to exercises list on success, or return to form on error
     */
    @RequestMapping(value = "/add-exercise", method = RequestMethod.POST)
    public String processExercise(@Valid @ModelAttribute("exercise") Exercise exercise,
                                 BindingResult result,
                                 Model model) {
        LOGGER.debug("+processExercise({}, {})", exercise, result);

        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "addExercise";
        }

        try {
            Exercise saved = exerciseService.saveExercise(exercise);
            LOGGER.info("Exercise saved successfully with ID: {}", saved.getId());
            return "redirect:/exercises";
        } catch (Exception e) {
            LOGGER.error("Error saving exercise: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while saving the exercise.");
            return "addExercise";
        }
    }

    /**
     * Display list of exercises.
     * 
     * @param model the model
     * @return the exercises list view
     */
    @RequestMapping(value = "/exercises", method = RequestMethod.GET)
    public String listExercises(Model model) {
        LOGGER.debug("+listExercises({})", model);
        List<Exercise> exercises = exerciseService.findAllExercises();
        model.addAttribute("exercises", exercises);
        LOGGER.debug("-listExercises(), found {} exercises", exercises.size());
        return "listActivities";
    }

    /**
     * Display exercise edit form.
     * 
     * @param id the exercise ID
     * @param model the model
     * @return the edit exercise form view
     */
    @RequestMapping(value = "/exercises/{id}/edit", method = RequestMethod.GET)
    public String editExercise(@PathVariable Long id, Model model) {
        LOGGER.debug("+editExercise({}, {})", id, model);
        model.addAttribute("activityTypes", com.rslakra.healthcare.healthsuite.model.ActivityType.values());
        Exercise exercise = exerciseService.findExerciseById(id);
        if (exercise == null) {
            LOGGER.warn("Exercise not found with ID: {}", id);
            return "redirect:/exercises";
        }
        model.addAttribute("exercise", exercise);
        LOGGER.debug("-editExercise()");
        return "editExercise";
    }

    /**
     * Process exercise update.
     * 
     * @param id the exercise ID
     * @param exercise the exercise data
     * @param result the binding result
     * @param model the model
     * @return redirect to exercises list on success
     */
    @RequestMapping(value = "/exercises/{id}", method = RequestMethod.POST)
    public String updateExercise(@PathVariable Long id,
                                @Valid @ModelAttribute("exercise") Exercise exercise,
                                BindingResult result,
                                Model model) {
        LOGGER.debug("+updateExercise({}, {})", id, exercise);

        if (result.hasErrors()) {
            LOGGER.debug("Validation errors found: {}", result.getAllErrors());
            return "editExercise";
        }

        exercise.setId(id);
        try {
            boolean updated = exerciseService.updateExercise(exercise);
            if (updated) {
                LOGGER.info("Exercise updated successfully with ID: {}", id);
                return "redirect:/exercises";
            } else {
                LOGGER.warn("Failed to update exercise with ID: {}", id);
                model.addAttribute("error", "Failed to update exercise.");
                return "editExercise";
            }
        } catch (Exception e) {
            LOGGER.error("Error updating exercise: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while updating the exercise.");
            return "editExercise";
        }
    }

    /**
     * Delete an exercise.
     * 
     * @param id the exercise ID
     * @return redirect to exercises list
     */
    @RequestMapping(value = "/exercises/{id}/delete", method = RequestMethod.POST)
    public String deleteExercise(@PathVariable Long id) {
        LOGGER.debug("+deleteExercise({})", id);
        try {
            boolean deleted = exerciseService.deleteExercise(id);
            if (deleted) {
                LOGGER.info("Exercise deleted successfully with ID: {}", id);
            } else {
                LOGGER.warn("Failed to delete exercise with ID: {}", id);
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting exercise: {}", e.getMessage(), e);
        }
        return "redirect:/exercises";
    }

    // REST API Endpoints

    /**
     * REST API: Create an exercise.
     * 
     * @param exercise the exercise data
     * @return ResponseEntity with success or error message
     */
    @RequestMapping(value = "/api/exercises", method = RequestMethod.POST,
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createExercise(@Valid @RequestBody Exercise exercise) {
        LOGGER.debug("+createExercise({})", exercise);

        Map<String, Object> response = new HashMap<>();

        try {
            Exercise saved = exerciseService.saveExercise(exercise);
            response.put("success", true);
            response.put("message", "Exercise created successfully");
            response.put("data", saved);
            LOGGER.debug("-createExercise()");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error creating exercise: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while creating the exercise: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * REST API: Get exercise by ID.
     * 
     * @param id the exercise ID
     * @return ResponseEntity with exercise data
     */
    @RequestMapping(value = "/api/exercises/{id}", method = RequestMethod.GET,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExercise(@PathVariable Long id) {
        LOGGER.debug("+getExercise({})", id);

        Map<String, Object> response = new HashMap<>();

        Exercise exercise = exerciseService.findExerciseById(id);
        if (exercise == null) {
            response.put("success", false);
            response.put("message", "Exercise not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("success", true);
        response.put("data", exercise);
        LOGGER.debug("-getExercise()");
        return ResponseEntity.ok(response);
    }

    /**
     * REST API: Get all exercises.
     * 
     * @return ResponseEntity with list of exercises
     */
    @RequestMapping(value = "/api/exercises", method = RequestMethod.GET,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllExercises() {
        LOGGER.debug("+getAllExercises()");

        Map<String, Object> response = new HashMap<>();

        try {
            List<Exercise> exercises = exerciseService.findAllExercises();
            response.put("success", true);
            response.put("data", exercises);
            response.put("count", exercises.size());
            LOGGER.debug("-getAllExercises(), found {} exercises", exercises.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error getting exercises: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while retrieving exercises: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * REST API: Get exercises by user ID.
     * 
     * @param userId the user ID
     * @return ResponseEntity with list of exercises
     */
    @RequestMapping(value = "/api/exercises/user/{userId}", method = RequestMethod.GET,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExercisesByUserId(@PathVariable Long userId) {
        LOGGER.debug("+getExercisesByUserId({})", userId);

        Map<String, Object> response = new HashMap<>();

        try {
            List<Exercise> exercises = exerciseService.findExercisesByUserId(userId);
            response.put("success", true);
            response.put("data", exercises);
            response.put("count", exercises.size());
            LOGGER.debug("-getExercisesByUserId(), found {} exercises", exercises.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error getting exercises by user ID: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while retrieving exercises: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * REST API: Update an exercise.
     * 
     * @param id the exercise ID
     * @param exercise the exercise data
     * @return ResponseEntity with success or error message
     */
    @RequestMapping(value = "/api/exercises/{id}", method = RequestMethod.PUT,
                   consumes = MediaType.APPLICATION_JSON_VALUE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateExerciseApi(@PathVariable Long id,
                                                                 @Valid @RequestBody Exercise exercise) {
        LOGGER.debug("+updateExerciseApi({}, {})", id, exercise);

        Map<String, Object> response = new HashMap<>();

        exercise.setId(id);
        try {
            boolean updated = exerciseService.updateExercise(exercise);
            if (updated) {
                Exercise updatedExercise = exerciseService.findExerciseById(id);
                response.put("success", true);
                response.put("message", "Exercise updated successfully");
                response.put("data", updatedExercise);
                LOGGER.debug("-updateExerciseApi()");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update exercise");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            LOGGER.error("Error updating exercise: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while updating the exercise: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * REST API: Delete an exercise.
     * 
     * @param id the exercise ID
     * @return ResponseEntity with success or error message
     */
    @RequestMapping(value = "/api/exercises/{id}", method = RequestMethod.DELETE,
                   produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteExerciseApi(@PathVariable Long id) {
        LOGGER.debug("+deleteExerciseApi({})", id);

        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = exerciseService.deleteExercise(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Exercise deleted successfully");
                LOGGER.debug("-deleteExerciseApi()");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Exercise not found or could not be deleted");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting exercise: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "An error occurred while deleting the exercise: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

