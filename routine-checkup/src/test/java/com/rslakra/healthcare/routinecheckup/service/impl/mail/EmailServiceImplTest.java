package com.rslakra.healthcare.routinecheckup.service.impl.mail;

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import com.rslakra.healthcare.routinecheckup.service.mail.EmailType;
import com.rslakra.healthcare.routinecheckup.service.security.TokenService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.MailMessages;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.WebConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailServiceImpl
 *
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TokenService tokenService;

    @Mock
    private WebConstants webConstants;

    @Mock
    private MailMessages mailMessages;

    @InjectMocks
    private EmailServiceImpl emailService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setLogin("testuser");
        userEntity.setMail("test@example.com");
    }

    // ==================== sendMessage() Tests ====================

    @Test
    void testSendMessage_Success() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When & Then - Should not throw
        assertDoesNotThrow(() -> 
            emailService.sendMessage("test@example.com", "Subject", "Body")
        );

        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendMessage_MailException_ShouldThrow() {
        // Given
        MailException mailException = new org.springframework.mail.MailAuthenticationException("Authentication failed");
        doThrow(mailException).when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When & Then
        MailException thrown = assertThrows(MailException.class, () -> 
            emailService.sendMessage("test@example.com", "Subject", "Body")
        );

        assertEquals("Authentication failed", thrown.getMessage());
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendMessage_UnexpectedException_ShouldWrapInMailSendException() {
        // Given
        doThrow(new RuntimeException("Unexpected error"))
            .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When & Then - Should wrap in MailSendException
        MailSendException thrown = assertThrows(MailSendException.class, () -> 
            emailService.sendMessage("test@example.com", "Subject", "Body")
        );

        assertTrue(thrown.getMessage().contains("Email sending failed"));
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    // ==================== sendEmail() Tests ====================

    @Test
    void testSendEmail_Registration_Success() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        when(tokenService.generateRegistrationToken(any(UserEntity.class))).thenReturn("test-token-123");
        when(webConstants.getDomainName()).thenReturn("localhost");
        when(webConstants.getAppPort()).thenReturn(8080);
        when(webConstants.getBasePath()).thenReturn("/routine-checkup");
        when(mailMessages.getCompletionMessageBodyTemplate()).thenReturn("Please complete registration: %s");
        when(mailMessages.getCompletionMessageSubject()).thenReturn("Complete Your Registration");

        // When
        String token = emailService.sendEmail(EmailType.REGISTRATION, userEntity, null);

        // Then
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(tokenService, times(1)).generateRegistrationToken(any(UserEntity.class));
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Registration_EmailFailure_ShouldNotThrow() {
        // Given - Email sending will fail with MailAuthenticationException
        doThrow(new org.springframework.mail.MailAuthenticationException("Authentication failed"))
            .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        when(tokenService.generateRegistrationToken(any(UserEntity.class))).thenReturn("test-token-123");
        when(webConstants.getDomainName()).thenReturn("localhost");
        when(webConstants.getAppPort()).thenReturn(8080);
        when(webConstants.getBasePath()).thenReturn("/routine-checkup");
        when(mailMessages.getCompletionMessageBodyTemplate()).thenReturn("Please complete registration: %s");
        when(mailMessages.getCompletionMessageSubject()).thenReturn("Complete Your Registration");

        // When & Then - Should not throw exception, token should still be returned
        String token = assertDoesNotThrow(() -> 
            emailService.sendEmail(EmailType.REGISTRATION, userEntity, null)
        );

        // Then - Token should still be returned even though email failed
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(tokenService, times(1)).generateRegistrationToken(any(UserEntity.class));
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Registration_UnexpectedException_ShouldNotThrow() {
        // Given - Email sending will fail with unexpected RuntimeException
        doThrow(new RuntimeException("Unexpected error"))
            .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        when(tokenService.generateRegistrationToken(any(UserEntity.class))).thenReturn("test-token-123");
        when(webConstants.getDomainName()).thenReturn("localhost");
        when(webConstants.getAppPort()).thenReturn(8080);
        when(webConstants.getBasePath()).thenReturn("/routine-checkup");
        when(mailMessages.getCompletionMessageBodyTemplate()).thenReturn("Please complete registration: %s");
        when(mailMessages.getCompletionMessageSubject()).thenReturn("Complete Your Registration");

        // When & Then - Should not throw exception
        String token = assertDoesNotThrow(() -> 
            emailService.sendEmail(EmailType.REGISTRATION, userEntity, null)
        );

        // Then - Token should still be returned even though email failed
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Login_Success() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        when(webConstants.getDomainName()).thenReturn("localhost");

        // When
        String result = emailService.sendEmail(EmailType.LOGIN, userEntity, null);

        // Then - LOGIN type doesn't return a token
        assertNull(result);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_PasswordReset_WithResetToken() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("resetToken", "reset-token-456");

        // When
        String result = emailService.sendEmail(EmailType.PASSWORD_RESET, userEntity, additionalData);

        // Then
        assertEquals("reset-token-456", result);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_AccountActivation_Success() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When
        String result = emailService.sendEmail(EmailType.ACCOUNT_ACTIVATION, userEntity, null);

        // Then - ACCOUNT_ACTIVATION type doesn't return a token
        assertNull(result);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Notification_WithCustomSubjectAndMessage() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("subject", "Custom Subject");
        additionalData.put("message", "Custom notification message");

        // When
        String result = emailService.sendEmail(EmailType.NOTIFICATION, userEntity, additionalData);

        // Then - NOTIFICATION type doesn't return a token
        assertNull(result);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_Notification_WithoutAdditionalData() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When
        String result = emailService.sendEmail(EmailType.NOTIFICATION, userEntity, null);

        // Then - Should use default subject and message
        assertNull(result);
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    // ==================== sendEmailAsync() Tests ====================

    @Test
    void testSendEmailAsync_Success() {
        // Given
        doNothing().when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When
        CompletableFuture<Void> future = emailService.sendEmailAsync(
            "test@example.com", "Subject", "Body", EmailType.REGISTRATION);

        // Then
        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmailAsync_MailFailure_ShouldNotThrow() {
        // Given
        doThrow(new org.springframework.mail.MailAuthenticationException("Authentication failed"))
            .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When & Then - Should not throw, should complete gracefully
        CompletableFuture<Void> future = assertDoesNotThrow(() -> 
            emailService.sendEmailAsync("test@example.com", "Subject", "Body", EmailType.REGISTRATION)
        );

        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void testSendEmailAsync_UnexpectedException_ShouldNotThrow() {
        // Given
        doThrow(new RuntimeException("Unexpected error"))
            .when(javaMailSender).send(any(org.springframework.mail.SimpleMailMessage.class));

        // When & Then - Should not throw, should complete gracefully
        CompletableFuture<Void> future = assertDoesNotThrow(() -> 
            emailService.sendEmailAsync("test@example.com", "Subject", "Body", EmailType.REGISTRATION)
        );

        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}

