package com.rslakra.healthcare.routinecheckup.service.impl.mail;

import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import com.rslakra.healthcare.routinecheckup.service.mail.MailComponent;
import com.rslakra.healthcare.routinecheckup.service.security.TokenComponent;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.MailMessages;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.WebConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationNotificationServiceImpl
 *
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@ExtendWith(MockitoExtension.class)
class RegistrationNotificationServiceImplTest {

    @Mock
    private TokenComponent tokenComponent;

    @Mock
    private MailComponent mailComponent;

    @Mock
    private WebConstants webConstants;

    @Mock
    private MailMessages mailMessages;

    @InjectMocks
    private RegistrationNotificationServiceImpl registrationNotificationService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setLogin("testuser");
        userEntity.setMail("test@example.com");

        when(tokenComponent.generateRegistrationToken(any(UserEntity.class))).thenReturn("test-token-123");
        when(webConstants.getDomainName()).thenReturn("localhost");
        when(webConstants.getAppPort()).thenReturn(8080);
        when(webConstants.getBasePath()).thenReturn("/routine-checkup");
        when(mailMessages.getCompletionMessageBodyTemplate()).thenReturn("Please complete registration: %s");
        when(mailMessages.getCompletionMessageSubject()).thenReturn("Complete Your Registration");
    }

    @Test
    void testSendRegistrationEmail_Success() {
        // When
        String token = registrationNotificationService.sendRegistrationEmail(userEntity);

        // Then
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(tokenComponent, times(1)).generateRegistrationToken(any(UserEntity.class));
        verify(mailComponent, times(1)).sendMessage(
            eq("test@example.com"),
            eq("Complete Your Registration"),
            anyString()
        );
    }

    @Test
    void testSendRegistrationEmail_EmailFailure_ShouldNotThrow() {
        // Given
        doThrow(new org.springframework.mail.MailAuthenticationException("Authentication failed"))
            .when(mailComponent).sendMessage(anyString(), anyString(), anyString());

        // When & Then - Should not throw exception
        String token = assertDoesNotThrow(() -> 
            registrationNotificationService.sendRegistrationEmail(userEntity)
        );

        // Then
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(mailComponent, times(1)).sendMessage(anyString(), anyString(), anyString());
    }

    @Test
    void testSendRegistrationEmail_UnexpectedException_ShouldNotThrow() {
        // Given
        doThrow(new RuntimeException("Unexpected error"))
            .when(mailComponent).sendMessage(anyString(), anyString(), anyString());

        // When & Then - Should not throw exception
        String token = assertDoesNotThrow(() -> 
            registrationNotificationService.sendRegistrationEmail(userEntity)
        );

        // Then
        assertNotNull(token);
        assertEquals("test-token-123", token);
        verify(mailComponent, times(1)).sendMessage(anyString(), anyString(), anyString());
    }
}

