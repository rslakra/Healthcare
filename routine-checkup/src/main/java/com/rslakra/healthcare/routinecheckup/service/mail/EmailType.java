package com.rslakra.healthcare.routinecheckup.service.mail;

/**
 * Email event types for different email notifications
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:24 PM
 */
public enum EmailType {

    /**
     * Registration email - sent when a new user registers
     */
    REGISTRATION,

    /**
     * Login notification - sent when user logs in (optional)
     */
    LOGIN,

    /**
     * Password reset - sent when user requests password reset
     */
    PASSWORD_RESET,

    /**
     * Account activation - sent when account is activated
     */
    ACCOUNT_ACTIVATION,

    /**
     * General notification - for other types of notifications
     */
    NOTIFICATION

}

