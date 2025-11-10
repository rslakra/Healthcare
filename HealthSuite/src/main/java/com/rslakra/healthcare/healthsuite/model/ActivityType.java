package com.rslakra.healthcare.healthsuite.model;

/**
 * Enumeration of activity types for exercises.
 * 
 * @author rslakra
 */
public enum ActivityType {
    RUN("Run", "Running"),
    BIKE("Bike", "Cycling"),
    SWIM("Swim", "Swimming"),
    WALK("Walk", "Walking"),
    JOG("Jog", "Jogging"),
    YOGA("Yoga", "Yoga"),
    STRENGTH_TRAINING("Strength Training", "Strength Training"),
    CARDIO("Cardio", "Cardio Exercise"),
    DANCE("Dance", "Dancing"),
    HIKING("Hiking", "Hiking"),
    TENNIS("Tennis", "Tennis"),
    BASKETBALL("Basketball", "Basketball"),
    SOCCER("Soccer", "Soccer"),
    SWIMMING_LAPS("Swimming Laps", "Swimming Laps"),
    ELLIPTICAL("Elliptical", "Elliptical Machine"),
    ROWING("Rowing", "Rowing"),
    PILATES("Pilates", "Pilates"),
    CROSSFIT("CrossFit", "CrossFit"),
    SPINNING("Spinning", "Spinning"),
    WEIGHT_LIFTING("Weight Lifting", "Weight Lifting");

    private final String code;
    private final String description;

    ActivityType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get ActivityType by code.
     * 
     * @param code the activity code
     * @return ActivityType or null if not found
     */
    public static ActivityType fromCode(String code) {
        for (ActivityType type : ActivityType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        return null;
    }
}

