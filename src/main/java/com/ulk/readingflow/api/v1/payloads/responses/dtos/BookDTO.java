package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.utils.SystemUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO implements Serializable {

    private String title;
    private String description;
    private Integer pages;

    @JsonProperty("gDriveID")
    private String gDriveID;
    private String isbn;

    private GenreDTO genre;
    private Set<CategoryDTO> categories;
    private List<AuthorDTO> authors;

    public static BookDTO fromEntity(Book book) {
        return BookDTO.builder()
                .title(book.getTitle())
                .description(book.getDescription())
                .pages(book.getPages())
                .gDriveID(book.getGDriveID())
                .isbn(book.getIsbn())

                .genre(book.getGenre() != null ?
                        GenreDTO.fromEntity(book.getGenre()) : null)

                .categories(book.getCategories() != null ?
                        book.getCategories().stream()
                                .map(CategoryDTO::fromEntity)
                                .collect(Collectors.toSet()) : null)

                .authors(book.getAuthors() != null ?
                        book.getAuthors().stream()
                                .map(AuthorDTO::fromEntity).toList() : null)

                .build();
    }

    @JsonIgnore
    public String getPagesReport() {
        if (this.pages == null || this.pages.equals(0)) {
            return "N/A";
        }
        return this.pages.toString();
    }

    @JsonIgnore
    public String getDescriptionReport() {
        if (this.description == null) {
            return "N/A";
        }
        return this.description.length() > 200 ?
                this.description.substring(0, 200) : this.description;
    }

    @JsonIgnore
    public String getAuthorsReport() {
        if (authors == null || authors.isEmpty()) {
            return "N/A";
        }

        List<String> authorNames = authors.stream()
                .map(AuthorDTO::getName).toList();

        return SystemUtils.joinStrings(authorNames);
    }

    @JsonIgnore
    public String getCategoriesReport() {
        if (categories == null || categories.isEmpty()) {
            return "N/A";
        }

        List<String> categoryNames = categories.stream()
                .map(CategoryDTO::getName).toList();

        return SystemUtils.joinStrings(categoryNames);
    }
}
