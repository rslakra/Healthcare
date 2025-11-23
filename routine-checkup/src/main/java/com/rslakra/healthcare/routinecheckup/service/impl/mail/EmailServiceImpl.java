package com.rslakra.healthcare.routinecheckup.service.impl.mail;

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import com.rslakra.healthcare.routinecheckup.service.mail.EmailService;
import com.rslakra.healthcare.routinecheckup.service.mail.EmailType;
import com.rslakra.healthcare.routinecheckup.service.security.TokenService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.MailMessages;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.WebConstants;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Unified email service implementation for handling all email notifications
 *
 * @author Rohtash Lakra
 * @created 8/12/21 4:21 PM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TokenService tokenService;
    private final WebConstants webConstants;
    private final MailMessages mailMessages;

    @Override
    public void sendMessage(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.debug("Email sent successfully to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to '{}'. Error: {}. " +
                    "Please check your email configuration in application.properties: " +
                    "spring.mail.host, spring.mail.port, spring.mail.username, spring.mail.password. " +
                    "For Gmail, you may need to use an App Password instead of your regular password. " +
                    "Registration/login will continue, but the user will not receive the email notification.",
                    to, e.getMessage(), e);
            // Re-throw MailException so caller can handle it appropriately
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while sending email to '{}': {}", to, e.getMessage(), e);
            // Wrap in MailException for consistent handling
            throw new org.springframework.mail.MailSendException("Email sending failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String sendEmail(EmailType emailType, UserEntity userEntity, Map<String, Object> additionalData) {
        String token = null;
        String subject = null;
        String body = null;
        String recipientEmail = userEntity.getMail();

        try {
            switch (emailType) {
                case REGISTRATION:
                    token = tokenService.generateRegistrationToken(userEntity);
                    String baseUrlPattern = "https://%s:%d%s";
                    String baseUrl = String.format(
                        baseUrlPattern,
                        webConstants.getDomainName(),
                        webConstants.getAppPort(),
                        webConstants.getBasePath()
                    );
                    String url = baseUrl + ViewNames.REGISTRATION_URL + "/" + token;
                    String messageBodyTemp = mailMessages.getCompletionMessageBodyTemplate();
                    body = String.format(messageBodyTemp, url);
                    subject = mailMessages.getCompletionMessageSubject();
                    break;

                case LOGIN:
                    // Future: Login notification email
                    subject = "Login Notification";
                    body = String.format("You have successfully logged in to your account at %s.", 
                        webConstants.getDomainName());
                    break;

                case PASSWORD_RESET:
                    // Future: Password reset email
                    if (additionalData != null && additionalData.containsKey("resetToken")) {
                        token = String.valueOf(additionalData.get("resetToken"));
                    }
                    subject = "Password Reset Request";
                    body = String.format("Please use the following link to reset your password: %s", token);
                    break;

                case ACCOUNT_ACTIVATION:
                    // Future: Account activation email
                    subject = "Account Activated";
                    body = "Your account has been successfully activated.";
                    break;

                case NOTIFICATION:
                    // Generic notification
                    subject = additionalData != null && additionalData.containsKey("subject") 
                        ? String.valueOf(additionalData.get("subject")) 
                        : "Notification";
                    body = additionalData != null && additionalData.containsKey("message")
                        ? String.valueOf(additionalData.get("message"))
                        : "You have a new notification.";
                    break;

                default:
                    log.warn("Unknown email type: {}", emailType);
                    return null;
            }

            // Send email asynchronously to avoid blocking the request
            sendEmailAsync(recipientEmail, subject, body, emailType);
            log.info("{} email queued for sending to: {}", emailType, recipientEmail);
            return token;

        } catch (Exception e) {
            log.error("Unexpected error while preparing {} email for '{}': {}",
                    emailType, recipientEmail, e.getMessage(), e);
            // Don't re-throw - allow operation to succeed even if email preparation fails
            return token;
        }
    }

    /**
     * Sends email asynchronously to avoid blocking the main request thread
     * Uses the dedicated emailTaskExecutor thread pool
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body, EmailType emailType) {
        try {
            sendMessage(to, subject, body);
            log.info("{} email sent successfully to: {}", emailType, to);
            return CompletableFuture.completedFuture(null);
        } catch (MailException e) {
            log.warn("{} email could not be sent to '{}', but operation will continue. Error: {}",
                    emailType, to, e.getMessage());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Unexpected error while sending {} email to '{}': {}",
                    emailType, to, e.getMessage(), e);
            return CompletableFuture.completedFuture(null);
        }
    }
}

