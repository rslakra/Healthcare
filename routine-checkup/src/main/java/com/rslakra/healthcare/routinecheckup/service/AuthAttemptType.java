package com.rslakra.healthcare.routinecheckup.service;

/**
 * Authentication attempt types for tracking different authentication events
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:16 PM
 */
public enum AuthAttemptType {

    /**
     * Login attempt - when user attempts to log in
     */
    LOGIN,

    /**
     * Registration attempt - when user attempts to register
     */
    REGISTRATION,

    /**
     * Password reset attempt - when user attempts to reset password
     */
    PASSWORD_RESET

}

