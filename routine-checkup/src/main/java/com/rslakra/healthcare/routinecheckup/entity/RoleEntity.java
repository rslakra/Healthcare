package com.rslakra.healthcare.routinecheckup.entity;

import lombok.*;

import jakarta.persistence.*;
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
