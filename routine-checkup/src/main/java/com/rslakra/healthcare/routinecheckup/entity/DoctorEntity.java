package com.rslakra.healthcare.routinecheckup.entity;

import lombok.*;

import javax.persistence.*;
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
