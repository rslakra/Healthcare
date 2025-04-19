package com.rslakra.healthcare.routinecheckup.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:09 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientResponseDto {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @JsonProperty("id")
    private String id;

    @JsonProperty("disease")
    private String disease;

    @JsonProperty("disease_onset_time")
    private Date diseaseOnsetTime;

    @JsonProperty("end_time_of_illness")
    private Date endTimeOfIllness;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("doctor")
    private DoctorResponseDto doctor;

    public String getDiseaseOnsetTimeFormatted() {
        Date diseaseOnsetTime = getDiseaseOnsetTime();
        if (diseaseOnsetTime == null) {
            return null;
        }

        return DATE_FORMAT.format(diseaseOnsetTime);
    }

    public String getEndTimeOfIllnessFormatted() {
        Date endTimeOfIllness = getEndTimeOfIllness();
        if (endTimeOfIllness == null) {
            return null;
        }

        return DATE_FORMAT.format(getEndTimeOfIllness());
    }

}
