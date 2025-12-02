package com.rslakra.healthcare.routinecheckup.utils.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Healthcare Roles Enum
 * 
 * @author Rohtash Lakra
 * @created 8/12/21 4:39 PM
 */
@AllArgsConstructor
@Getter
public enum Roles {

    /**
     * Administrator - Full system access and management capabilities
     */
    ADMIN("ADMIN"),
    
    /**
     * Doctor - Medical expert who diagnoses illnesses, develops treatment plans, 
     * and oversees overall medical care. Can be a specialist in specific areas.
     */
    DOCTOR("DOCTOR"),
    
    /**
     * Nurse - Direct care provider who administers medications, monitors patient 
     * conditions, and coordinates care among healthcare professionals.
     */
    NURSE("NURSE"),
    
    /**
     * Patient - Active participant in their own care, provides information about 
     * symptoms and medical history, and makes decisions about their treatment.
     */
    PATIENT("PATIENT");

    private final String value;

}

