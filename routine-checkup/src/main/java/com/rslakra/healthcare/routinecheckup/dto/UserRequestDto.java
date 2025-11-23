package com.rslakra.healthcare.routinecheckup.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.rslakra.healthcare.routinecheckup.utils.constants.Patterns;
import com.rslakra.healthcare.routinecheckup.utils.validation.constraint.group.user.CreateUserValidationGroup;
import com.rslakra.healthcare.routinecheckup.utils.validation.constraint.group.user.UpdateUserByAdminValidationGroup;
import com.rslakra.healthcare.routinecheckup.utils.validation.constraint.group.user.UpdateUserValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;


/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:07 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestDto {

    @JsonProperty("id")
    @NotNull(groups = UpdateUserByAdminValidationGroup.class)
    @Null
    @Pattern(regexp = Patterns.UUID_PATTERN)
    private String id;

    @JsonProperty("login")
    @NotEmpty(groups = CreateUserValidationGroup.class)
    @Null(groups = UpdateUserValidationGroup.class)
    private String login;

    @JsonProperty("pass")
    @NotEmpty(groups = CreateUserValidationGroup.class)
    private String password;

    @JsonProperty("first_name")
    @NotEmpty(groups = CreateUserValidationGroup.class)
    private String firstName;

    @JsonProperty("last_name")
    @NotEmpty(groups = CreateUserValidationGroup.class)
    private String lastName;

    @JsonProperty("mail")
    @NotEmpty(groups = CreateUserValidationGroup.class)
    @Null(groups = UpdateUserValidationGroup.class)
    @Email
    private String mail;

    @JsonProperty("role")
    private String role; // Role selection for registration (PATIENT, DOCTOR, NURSE, etc.)

    @JsonProperty("specialization")
    private String specialization; // Specialization for DOCTOR or NURSE roles

}
