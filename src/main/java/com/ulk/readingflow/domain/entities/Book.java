package com.ulk.readingflow.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKS")
@Entity(name = "BOOK")
public class Book extends AbstractEntity {

    @Column(
            name = "TITLE",
            unique = true
    )
    private String title;

    @Column(
            name = "DESCRIPTION",
            columnDefinition = "MEDIUMTEXT"
    )
    private String description;

    @Column(name = "PAGES")
    private Integer pages;

    @JsonIgnoreProperties("books")
    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "books"
    )
    private List<Author> authors;

    @Column(name = "GDRIVE_ID")
    private String gDriveID;

    @Column(name = "ISBN")
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "GENRE_ID",
            referencedColumnName = "ID"
    )
    private Genre genre;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "BOOK_CATEGORY",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    private Set<Category> categories;

    public void addAuthor(Author author) {
        if (null == this.authors) {
            this.authors = new ArrayList<>();
        }
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author authorToRemove) {
        if (this.authors != null) {
            this.authors.remove(authorToRemove);
            authorToRemove.getBooks().remove(this);
        }
    }
}
