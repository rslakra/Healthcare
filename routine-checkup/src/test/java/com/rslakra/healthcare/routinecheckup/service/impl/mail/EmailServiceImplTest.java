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
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

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
    void testSendRegistrationEmail_Success() {
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
    void testSendEmail_RegistrationType() {
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
        verify(javaMailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}

