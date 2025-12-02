package com.rslakra.healthcare.routinecheckup.service.mail;

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;

import java.util.Map;

/**
 * Unified email service for handling all email notifications
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:21 PM
 */
public interface EmailService {

    /**
     * Sends a generic email message
     *
     * @param to      Recipient email address
     * @param subject Email subject
     * @param body    Email body
     */
    void sendMessage(String to, String subject, String body);

    /**
     * Sends an email based on the email type
     *
     * @param emailType The type of email to send
     * @param userEntity The user entity (required for most email types)
     * @param additionalData Optional map of additional data (e.g., "resetToken" -> token, "customMessage" -> message)
     *                       Can be null or empty for email types that don't require additional data
     * @return Optional token or identifier generated for the email (e.g., registration token)
     */
    String sendEmail(EmailType emailType, UserEntity userEntity, Map<String, Object> additionalData);

}

