package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.ulk.readingflow.domain.entities.Category;
import com.ulk.readingflow.domain.entities.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDTO implements Serializable {

    private String name;
    private String icon;
    private List<String> categories;

    public static GenreDTO fromEntity(Genre genre) {
        return genre.getCategories().isEmpty() ?
                GenreDTO.builder()
                        .name(genre.getName())
                        .icon(genre.getIcon())
                        .categories(new ArrayList<>())
                        .build() :
                GenreDTO.builder()
                        .name(genre.getName())
                        .icon(genre.getIcon())
                        .categories(
                                genre.getCategories().stream()
                                        .map(Category::getName).toList())
                        .build();
    }
}
