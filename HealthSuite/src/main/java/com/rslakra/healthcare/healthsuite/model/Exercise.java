package com.rslakra.healthcare.healthsuite.model;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Exercise model for database operations and form handling.
 * Represents an exercise record in the exercises table.
 * 
 * @author rslakra
 */
public class Exercise {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Activity type is required")
    @Size(max = 64, message = "Activity type must not exceed 64 characters")
    private String activity;

    @NotNull(message = "Minutes is required")
    @Min(value = 1, message = "Minutes must be at least 1")
    @Range(min = 1, max = 120, message = "Minutes must be between 1 and 120")
    private Integer minutes;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Exercise date is required")
    private LocalDate exerciseDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Exercise() {
    }

    public Exercise(Long userId, String activity, Integer minutes, 
                   String description, LocalDate exerciseDate) {
        this.userId = userId;
        this.activity = activity;
        this.minutes = minutes;
        this.description = description;
        this.exerciseDate = exerciseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(LocalDate exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
