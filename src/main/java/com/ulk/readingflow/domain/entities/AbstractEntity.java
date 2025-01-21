package com.ulk.readingflow.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(
            name = "CREATED_AT",
            columnDefinition = "TIMESTAMP"
    )
    @CreationTimestamp
    @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(
            name = "UPDATED_AT",
            columnDefinition = "TIMESTAMP"
    )
    @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
