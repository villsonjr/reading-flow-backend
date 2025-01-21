package com.ulk.readingflow.domain.entities;

import com.ulk.readingflow.domain.enumerations.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity(name = "ROLE")
@Table(name = "ROLES")
public class Role implements GrantedAuthority, Serializable {

    @Id
    @Column(
            name = "ID",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(
            name = "DESCRIPTION",
            length = 20
    )
    @Enumerated(EnumType.STRING)
    private RoleEnum description;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.description.getDescription();
    }
}
