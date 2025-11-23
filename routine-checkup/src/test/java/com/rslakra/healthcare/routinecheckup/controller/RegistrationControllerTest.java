package com.rslakra.healthcare.routinecheckup.controller;

import com.rslakra.healthcare.routinecheckup.dto.UserRequestDto;
import com.rslakra.healthcare.routinecheckup.dto.response.UserResponseDto;
import static com.rslakra.healthcare.routinecheckup.dto.response.UserResponseDto.builder;
import com.rslakra.healthcare.routinecheckup.dto.request.DoctorRequestDto;
import com.rslakra.healthcare.routinecheckup.service.DoctorService;
import com.rslakra.healthcare.routinecheckup.service.AuthAttemptsService;
import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.CaptchaConstants;
import com.rslakra.healthcare.routinecheckup.utils.constants.ModelAttributesNames;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import com.rslakra.healthcare.routinecheckup.utils.security.Roles;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationController
 *
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private CaptchaConstants captchaConstants;

    @Mock
    private UserService userService;

    @Mock
    private AuthAttemptsService authAttemptsService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Principal principal;

    @InjectMocks
    private RegistrationController registrationController;

    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setLogin("testuser");
        userRequestDto.setPassword("password123");
        userRequestDto.setFirstName("Test");
        userRequestDto.setLastName("User");
        userRequestDto.setMail("test@example.com");
        userRequestDto.setRole("PATIENT");

        userResponseDto = UserResponseDto.builder()
            .id(UUID.randomUUID().toString())
            .login("testuser")
            .firstName("Test")
            .lastName("User")
            .build();
    }

    @Test
    void testRegistrationView_WhenNotAuthenticated() {
        // Given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(authAttemptsService.isExtraCurrentRegistration(anyString())).thenReturn(false);
        when(captchaConstants.getSiteKey()).thenReturn("test-site-key");

        // When
        String viewName = registrationController.registrationView(model, request, null);

        // Then
        assertEquals(ViewNames.REGISTRATION_VIEW_NAME, viewName);
        verify(model, times(1)).addAttribute(eq(ModelAttributesNames.REGISTRATION_DTO), any(UserRequestDto.class));
        verify(model, times(1)).addAttribute(eq(ModelAttributesNames.CAPTCHA_SITE_KEY), eq("test-site-key"));
    }

    @Test
    void testRegistrationView_WhenAuthenticated_ShouldRedirect() {
        // When
        String viewName = registrationController.registrationView(model, request, principal);

        // Then
        assertEquals("redirect:" + ViewNames.DOCTORS_AND_PATIENTS_LIST_URL, viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testRegistration_Patient_Success() {
        // Given
        userRequestDto.setRole("PATIENT");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userService.registerNewUser(any(), any(), any(), anyString())).thenReturn(userResponseDto);

        // When
        String viewName = registrationController.registration(
            userRequestDto, null, request
        );

        // Then
        assertEquals(ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME, viewName);
        verify(userService, times(1)).registerNewUser(
            eq(userRequestDto), isNull(), eq(Roles.PATIENT), eq("127.0.0.1")
        );
        verify(doctorService, never()).saveDoctor(any(), anyString());
    }

    @Test
    void testRegistration_Doctor_WithSpecialization() {
        // Given
        userRequestDto.setRole("DOCTOR");
        userRequestDto.setSpecialization("Cardiology");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userService.registerNewUser(any(), any(), any(), anyString())).thenReturn(userResponseDto);

        // When
        String viewName = registrationController.registration(
            userRequestDto, null, request
        );

        // Then
        assertEquals(ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME, viewName);
        verify(userService, times(1)).registerNewUser(
            eq(userRequestDto), isNull(), eq(Roles.DOCTOR), eq("127.0.0.1")
        );
        verify(doctorService, times(1)).saveDoctor(any(DoctorRequestDto.class), eq("testuser"));
    }

    @Test
    void testRegistration_Doctor_WithoutSpecialization() {
        // Given
        userRequestDto.setRole("DOCTOR");
        userRequestDto.setSpecialization(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userService.registerNewUser(any(), any(), any(), anyString())).thenReturn(userResponseDto);

        // When
        String viewName = registrationController.registration(
            userRequestDto, null, request
        );

        // Then
        assertEquals(ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME, viewName);
        verify(doctorService, never()).saveDoctor(any(), anyString());
    }

    @Test
    void testRegistration_InvalidRole_ShouldDefaultToPatient() {
        // Given
        userRequestDto.setRole("INVALID_ROLE");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userService.registerNewUser(any(), any(), any(), anyString())).thenReturn(userResponseDto);

        // When
        String viewName = registrationController.registration(
            userRequestDto, null, request
        );

        // Then
        assertEquals(ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME, viewName);
        verify(userService, times(1)).registerNewUser(
            eq(userRequestDto), isNull(), eq(Roles.PATIENT), eq("127.0.0.1")
        );
    }

    @Test
    void testRegistration_EmailFailure_ShouldStillSucceed() {
        // Given
        userRequestDto.setRole("PATIENT");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        // Simulate email failure - registration should still succeed
        when(userService.registerNewUser(any(), any(), any(), anyString())).thenReturn(userResponseDto);

        // When
        String viewName = registrationController.registration(
            userRequestDto, null, request
        );

        // Then
        assertEquals(ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME, viewName);
        verify(userService, times(1)).registerNewUser(any(), any(), any(), anyString());
    }
}

