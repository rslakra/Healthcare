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
import java.util.UUID;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:01 PM
 */
@Entity
@Table(name = "doctors")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DoctorEntity {

    @Id
    private UUID id;

    @Column(name = "speciality")
    private String speciality;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
    }

}
