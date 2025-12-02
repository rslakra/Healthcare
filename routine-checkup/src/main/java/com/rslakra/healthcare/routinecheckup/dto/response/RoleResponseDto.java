package com.rslakra.healthcare.routinecheckup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("role_name")
    private String roleName;

}

