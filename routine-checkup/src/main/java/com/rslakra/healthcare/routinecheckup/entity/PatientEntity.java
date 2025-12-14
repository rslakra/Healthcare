package com.rslakra.healthcare.routinecheckup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:01 PM
 */
@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PatientEntity {

    @Id
    private UUID id;

    @Column(name = "disease")
    private String disease;

    @Column(name = "disease_onset_time")
    private Date diseaseOnsetTime;

    @Column(name = "end_time_of_illness")
    private Date endTimeOfIllness;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorEntity doctor;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
    }

}
