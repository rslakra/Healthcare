package com.rslakra.healthcare.routinecheckup.service.impl;

import com.rslakra.healthcare.routinecheckup.dto.UserRequestDto;
import com.rslakra.healthcare.routinecheckup.dto.response.UserResponseDto;
import com.rslakra.healthcare.routinecheckup.entity.RoleEntity;
import com.rslakra.healthcare.routinecheckup.entity.UserEntity;
import com.rslakra.healthcare.routinecheckup.repository.UserRepository;
import com.rslakra.healthcare.routinecheckup.service.CaptchaService;
import com.rslakra.healthcare.routinecheckup.service.RoleService;
import com.rslakra.healthcare.routinecheckup.service.AuthAttemptsService;
import com.rslakra.healthcare.routinecheckup.service.mail.EmailService;
import com.rslakra.healthcare.routinecheckup.service.mail.EmailType;
import com.rslakra.healthcare.routinecheckup.utils.components.DtoUtils;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.FileStorageConstants;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.Messages;
import com.rslakra.healthcare.routinecheckup.utils.security.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 *
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Messages messages;

    @Mock
    private DtoUtils dtoUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private RoleService roleService;

    @Mock
    private AuthAttemptsService authAttemptsService;

    @Mock
    private EmailService emailService;

    @Mock
    private FileStorageConstants fileStorageConstants;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private UserEntity userEntity;
    private UserResponseDto userResponseDto;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        // Setup test data
        userRequestDto = new UserRequestDto();
        userRequestDto.setLogin("testuser");
        userRequestDto.setPassword("password123");
        userRequestDto.setFirstName("Test");
        userRequestDto.setLastName("User");
        userRequestDto.setMail("test@example.com");

        roleEntity = new RoleEntity();
        roleEntity.setId(UUID.randomUUID());
        roleEntity.setRoleName(Roles.PATIENT.getValue());

        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setLogin("testuser");
        userEntity.setPassword("encodedPassword");
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
        userEntity.setMail("test@example.com");
        userEntity.setRole(roleEntity);
        userEntity.setIsTemporary(true);

        userResponseDto = UserResponseDto.builder()
            .id(userEntity.getId().toString())
            .login("testuser")
            .firstName("Test")
            .lastName("User")
            .build();
    }

    @Test
    void testRegisterNewUser_Success() {
        // Given
        when(authAttemptsService.isExtraLastRegistration(anyString())).thenReturn(false);
        when(dtoUtils.sanitizeUser(any(UserRequestDto.class))).thenReturn(userRequestDto);
        when(dtoUtils.convertUser(any(UserRequestDto.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleService.createIfNotExists(anyString())).thenReturn(roleEntity);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByMail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(emailService.sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any())).thenReturn("token123");
        when(dtoUtils.convertUser(any(UserEntity.class))).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.registerNewUser(
            userRequestDto, null, Roles.PATIENT, "127.0.0.1"
        );

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(emailService, times(1)).sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any());
    }

    @Test
    void testRegisterNewUser_EmailFailure_ShouldStillSucceed() {
        // Given
        when(authAttemptsService.isExtraLastRegistration(anyString())).thenReturn(false);
        when(dtoUtils.sanitizeUser(any(UserRequestDto.class))).thenReturn(userRequestDto);
        when(dtoUtils.convertUser(any(UserRequestDto.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleService.createIfNotExists(anyString())).thenReturn(roleEntity);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByMail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(emailService.sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any()))
            .thenThrow(new MailAuthenticationException("Authentication failed"));
        when(dtoUtils.convertUser(any(UserEntity.class))).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.registerNewUser(
            userRequestDto, null, Roles.PATIENT, "127.0.0.1"
        );

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(emailService, times(1)).sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any());
        // Registration should succeed even if email fails
    }

    @Test
    void testRegisterNewUser_DuplicateLogin_ShouldReturnExisting() {
        // Given
        when(authAttemptsService.isExtraLastRegistration(anyString())).thenReturn(false);
        when(dtoUtils.sanitizeUser(any(UserRequestDto.class))).thenReturn(userRequestDto);
        when(dtoUtils.convertUser(any(UserRequestDto.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleService.createIfNotExists(anyString())).thenReturn(roleEntity);
        when(userRepository.existsByLogin(anyString())).thenReturn(true);
        when(dtoUtils.convertUser(any(UserEntity.class))).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.registerNewUser(
            userRequestDto, null, Roles.PATIENT, "127.0.0.1"
        );

        // Then
        assertNotNull(result);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(emailService, never()).sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any());
    }

    @Test
    void testRegisterNewUser_DuplicateEmail_ShouldReturnExisting() {
        // Given
        when(authAttemptsService.isExtraLastRegistration(anyString())).thenReturn(false);
        when(dtoUtils.sanitizeUser(any(UserRequestDto.class))).thenReturn(userRequestDto);
        when(dtoUtils.convertUser(any(UserRequestDto.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleService.createIfNotExists(anyString())).thenReturn(roleEntity);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByMail(anyString())).thenReturn(true);
        when(dtoUtils.convertUser(any(UserEntity.class))).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.registerNewUser(
            userRequestDto, null, Roles.PATIENT, "127.0.0.1"
        );

        // Then
        assertNotNull(result);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(emailService, never()).sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any());
    }

    @Test
    void testRegisterNewUser_WithReCaptcha() {
        // Given
        when(authAttemptsService.isExtraLastRegistration(anyString())).thenReturn(true);
        when(dtoUtils.sanitizeUser(any(UserRequestDto.class))).thenReturn(userRequestDto);
        when(dtoUtils.convertUser(any(UserRequestDto.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleService.createIfNotExists(anyString())).thenReturn(roleEntity);
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByMail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(emailService.sendEmail(eq(EmailType.REGISTRATION), any(UserEntity.class), any())).thenReturn("token123");
        when(dtoUtils.convertUser(any(UserEntity.class))).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.registerNewUser(
            userRequestDto, "captcha-response", Roles.PATIENT, "127.0.0.1"
        );

        // Then
        assertNotNull(result);
        verify(captchaService, times(1)).approve("captcha-response");
    }
}

