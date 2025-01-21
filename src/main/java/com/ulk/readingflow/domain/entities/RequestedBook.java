package com.ulk.readingflow.domain.entities;

import com.ulk.readingflow.domain.enumerations.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "REQUESTED_BOOKS")
@Entity(name = "REQUESTED_BOOK")
public class RequestedBook extends AbstractEntity {

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHOR")
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS",
            nullable = false)
    private StatusEnum status;

    @Column(
            name = "STARTED_AT",
            columnDefinition = "TIMESTAMP"
    )
    @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime startedAt;

    @Column(
            name = "CLOSED_AT",
            columnDefinition = "TIMESTAMP"
    )
    @DateTimeFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime closedAt;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "OWNER_USER_ID",
            referencedColumnName = "ID"
    )
    private User ownerUser;

    @Column(name = "USER_IP")
    private String userIp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "ASSIGNED_USER_ID",
            referencedColumnName = "ID"
    )
    private User assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "BOOK_ID",
            referencedColumnName = "ID"
    )
    private Book book;
}
