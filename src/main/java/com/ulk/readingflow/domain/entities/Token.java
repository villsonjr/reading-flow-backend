package com.ulk.readingflow.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TOKEN")
@Table(name = "TOKENS")
public class Token extends AbstractEntity {

    @Column(name = "REVOKED")
    public boolean revoked;

    @Column(name = "EXPIRED")
    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User user;

    @Column(
            name = "TOKEN",
            nullable = false,
            unique = true
    )
    private String accessToken;

    @Column(name = "EXPIRATION_DATE")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expirationDate;

}
