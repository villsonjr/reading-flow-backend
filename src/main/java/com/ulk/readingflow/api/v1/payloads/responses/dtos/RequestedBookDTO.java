package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ulk.readingflow.domain.entities.RequestedBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestedBookDTO implements Serializable {

    private String key;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - hh:mm:ss")
    private String requestedDate;
    private String bookTitle;
    private String authorName;
    private String status;

    private UserDTO owner;
    private UserDTO assigned;

    private BookDTO book;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - hh:mm:ss")
    private String startedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - hh:mm:ss")
    private String closedDate;

    public static RequestedBookDTO fromEntity(RequestedBook requestedBook) {
        return RequestedBookDTO.builder()
                .key(requestedBook.getId().toString())
                .requestedDate(requestedBook.getCreatedAt().toString())
                .bookTitle(requestedBook.getTitle())
                .authorName(requestedBook.getAuthor())
                .status(requestedBook.getStatus().getDescription())

                .owner(UserDTO.fromEntity(requestedBook.getOwnerUser()))
                .assigned(UserDTO.fromEntity(requestedBook.getAssignedUser()))

                .book(requestedBook.getBook() != null ?
                        BookDTO.fromEntity(requestedBook.getBook()) :
                        BookDTO.builder().build())

                .startedDate(null != requestedBook.getStartedAt() ?
                        requestedBook.getStartedAt().toString() : null)
                .closedDate(null != requestedBook.getClosedAt() ?
                        requestedBook.getClosedAt().toString() : null)

                .build();
    }

    @JsonIgnore
    public String getMailStatus() {
        return switch (this.status) {
            case "CREATED" -> "criada";
            case "IN_PROGRESS" -> "iniciada";
            case "CANCELED" -> "cancelada";
            case "COMPLETE" -> "concluída";
            default -> "Erro Interno";
        };
    }
}
