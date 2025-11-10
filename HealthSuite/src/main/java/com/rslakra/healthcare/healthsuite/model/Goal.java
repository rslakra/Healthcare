package com.rslakra.healthcare.healthsuite.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * Goal model representing an exercise goal.
 * Maps to the goals table in the database.
 * 
 * @author rslakra
 */
public class Goal {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Activity type is required")
    @Size(max = 64, message = "Activity type must not exceed 64 characters")
    private String activityType;

    @NotNull(message = "Minutes is required")
    @Min(value = 1, message = "Minutes must be at least 1")
    @Range(min = 1, max = 120, message = "Minutes must be between 1 and 120")
    private Integer minutes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Goal() {
    }

    public Goal(Long userId, String activityType, Integer minutes) {
        this.userId = userId;
        this.activityType = activityType;
        this.minutes = minutes;
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

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
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
