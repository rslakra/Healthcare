package com.rslakra.healthcare.routinecheckup.service;

import com.rslakra.healthcare.routinecheckup.entity.UserLoginAttempts;
import com.rslakra.healthcare.routinecheckup.entity.UserRegistrationAttempts;

/**
 * Unified service for handling all authentication attempt tracking
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:16 PM
 */
public interface AuthAttemptsService {

    /**
     * Commits a new login attempt for the given IP address
     *
     * @param userIp The IP address of the user
     * @return The login attempts entity
     */
    UserLoginAttempts commitNewLoginAttempt(String userIp);

    /**
     * Commits a new registration attempt for the given IP address
     *
     * @param userIp The IP address of the user
     * @return The registration attempts entity
     */
    UserRegistrationAttempts commitNewRegistrationAttempt(String userIp);

    /**
     * Commits a new attempt based on the attempt type
     *
     * @param attemptType The type of authentication attempt
     * @param userIp The IP address of the user
     * @return Object representing the attempt (UserLoginAttempts or UserRegistrationAttempts)
     */
    Object commitNewAttempt(AuthAttemptType attemptType, String userIp);

    /**
     * Checks if the current login attempt exceeds the maximum allowed attempts
     *
     * @param userIp The IP address of the user
     * @return true if the attempt count exceeds the maximum
     */
    boolean isExtraCurrentLogin(String userIp);

    /**
     * Checks if the current registration attempt exceeds the maximum allowed attempts
     *
     * @param userIp The IP address of the user
     * @return true if the attempt count exceeds the maximum
     */
    boolean isExtraCurrentRegistration(String userIp);

    /**
     * Checks if the last registration attempt (without committing a new one) exceeds the maximum
     *
     * @param userIp The IP address of the user
     * @return true if the attempt count exceeds the maximum
     */
    boolean isExtraLastRegistration(String userIp);

    /**
     * Generic method to check if attempts exceed maximum based on attempt type
     *
     * @param attemptType The type of authentication attempt
     * @param userIp The IP address of the user
     * @param commitNewAttempt Whether to commit a new attempt before checking
     * @return true if the attempt count exceeds the maximum
     */
    boolean isExtraAttempt(AuthAttemptType attemptType, String userIp, boolean commitNewAttempt);

}

