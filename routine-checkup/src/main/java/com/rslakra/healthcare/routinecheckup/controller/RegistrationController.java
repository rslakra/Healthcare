package com.rslakra.healthcare.routinecheckup.controller;

import com.rslakra.healthcare.routinecheckup.dto.UserRequestDto;
import com.rslakra.healthcare.routinecheckup.dto.request.DoctorRequestDto;
import com.rslakra.healthcare.routinecheckup.service.DoctorService;
import com.rslakra.healthcare.routinecheckup.service.AuthAttemptsService;
import com.rslakra.healthcare.routinecheckup.service.UserService;
import com.rslakra.healthcare.routinecheckup.utils.components.holder.CaptchaConstants;
import com.rslakra.healthcare.routinecheckup.utils.constants.ModelAttributesNames;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import com.rslakra.healthcare.routinecheckup.utils.security.Roles;
import com.rslakra.healthcare.routinecheckup.utils.validation.constraint.group.user.CreateUserValidationGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:00 PM
 */
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private static final String CAPTCHA_RESPONSE_PARAM_NAME = "g-recaptcha-response";
    private final CaptchaConstants captchaConstants;
    private final UserService userService;
    private final AuthAttemptsService authAttemptsService;
    private final DoctorService doctorService;

    @GetMapping(value = ViewNames.REGISTRATION_URL)
    public String registrationView(Model model, HttpServletRequest request, Principal principal) {
        if (principal != null) {
            return "redirect:" + ViewNames.DOCTORS_AND_PATIENTS_LIST_URL;
        }

        model.addAttribute(ModelAttributesNames.REGISTRATION_DTO, new UserRequestDto());
        model.addAttribute(ModelAttributesNames.CAPTCHA_SITE_KEY, captchaConstants.getSiteKey());

        String userIp = request.getRemoteAddr();
        boolean extraCurrentRegistration = authAttemptsService.isExtraCurrentRegistration(userIp);
        model.addAttribute(ModelAttributesNames.IS_EXTRA_REGISTRATION, extraCurrentRegistration);

        return ViewNames.REGISTRATION_VIEW_NAME;
    }

    @GetMapping(value = ViewNames.REGISTRATION_URL + "/{registration_token}")
    public String completeRegistration(@PathVariable("registration_token") String registrationToken) {
        userService.completeRegistration(registrationToken);

        return "redirect:" + ViewNames.LOGIN_URL;
    }

    @PostMapping(value = ViewNames.REGISTRATION_URL)
    public String registration(@ModelAttribute(ModelAttributesNames.REGISTRATION_DTO) @Validated(CreateUserValidationGroup.class) UserRequestDto userRequestDto, @RequestParam(value = CAPTCHA_RESPONSE_PARAM_NAME, required = false) String captchaResponse, HttpServletRequest request) {
        String userIp = request.getRemoteAddr();
        
        // Determine role from DTO or default to PATIENT
        Roles selectedRole = Roles.PATIENT; // Default to PATIENT
        if (userRequestDto.getRole() != null && !userRequestDto.getRole().trim().isEmpty()) {
            try {
                selectedRole = Roles.valueOf(userRequestDto.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid role, default to PATIENT
                selectedRole = Roles.PATIENT;
            }
        }
        
        // Register the user
        var userResponse = userService.registerNewUser(userRequestDto, captchaResponse, selectedRole, userIp);
        
        // If user registered as DOCTOR and provided specialization, create doctor record
        // Note: User is temporary at this point, but doctor record can still be created
        // It will be visible once user confirms email and becomes non-temporary
        if (selectedRole == Roles.DOCTOR && userRequestDto.getSpecialization() != null && !userRequestDto.getSpecialization().trim().isEmpty()) {
            try {
                DoctorRequestDto doctorDto = new DoctorRequestDto();
                doctorDto.setSpeciality(userRequestDto.getSpecialization().trim());
                doctorDto.setUserId(userResponse.getId());
                // Create doctor record - user login will be available after email confirmation
                // We'll use the user ID which is already available
                doctorService.saveDoctor(doctorDto, userRequestDto.getLogin());
            } catch (Exception e) {
                // Log error but don't fail registration
                // Doctor can be added later through the UI after email confirmation
            }
        }

        return ViewNames.PRE_COMPLETE_REGISTRATION_VIEW_NAME;
    }

}
