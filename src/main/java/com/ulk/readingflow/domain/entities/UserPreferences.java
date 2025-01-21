package com.ulk.readingflow.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_PREFERENCES")
@Entity(name = "USER_PREFERENCES")
public class UserPreferences {

    @Id
    @Column(
            name = "ID",
            nullable = false,
            updatable = false
    )
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "PREFERENCE_KEY")
    private String key;

    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

}
