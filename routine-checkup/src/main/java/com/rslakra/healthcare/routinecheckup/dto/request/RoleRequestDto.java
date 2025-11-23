package com.rslakra.healthcare.routinecheckup.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import com.rslakra.healthcare.routinecheckup.utils.constants.Patterns;

/**
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {

    @JsonProperty("id")
    @Pattern(regexp = Patterns.UUID_PATTERN, message = "Invalid UUID format")
    private String id;

    @JsonProperty("role_name")
    @NotEmpty(message = "Role name is required")
    private String roleName;

}

