package com.ulk.readingflow.api.v1.payloads.requests.dtos;

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
public class RequestBookDTO implements Serializable {

    private String bookTitle;
    private String authorName;

    public static RequestBookDTO fromEntity(RequestedBook requestedBook) {
        return RequestBookDTO.builder()
                .bookTitle(requestedBook.getTitle())
                .authorName(requestedBook.getAuthor())
                .build();
    }
}
