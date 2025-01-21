package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.ulk.readingflow.domain.entities.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO implements Serializable {

    private String name;

    public static AuthorDTO fromEntity(Author author) {
        return AuthorDTO.builder()
                .name(author.getName())
                .build();
    }

    public static List<AuthorDTO> fromEntityList(List<Author> authors) {
        return authors.stream()
                .map(AuthorDTO::fromEntity)
                .toList();
    }
}
