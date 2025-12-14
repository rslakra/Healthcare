package com.rslakra.healthcare.routinecheckup.entity;

import lombok.Getter;
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
 * @created 8/12/21 4:02 PM
 */
@Entity
@Table(name = "services_schedule")
@Getter
@Setter
public class ServiceScheduleEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "type")
    private String serviceType;

    @Column(name = "additional_data")
    private String additionalData;

    @Column(name = "creation_date")
    private Date creationDate;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
        creationDate = new Date();
    }

}
