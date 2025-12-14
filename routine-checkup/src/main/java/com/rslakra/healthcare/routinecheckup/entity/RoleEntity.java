package com.rslakra.healthcare.routinecheckup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * @author Rohtash Lakra
 * @created 8/12/21 4:01 PM
 */
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {

    @Id
    private UUID id;

    @Column(name = "role_name")
    private String roleName;

    @PrePersist
    public void prePersist() {
        id = UUID.randomUUID();
    }

}
