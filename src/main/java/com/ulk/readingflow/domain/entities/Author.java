package com.ulk.readingflow.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "AUTHORS")
@Entity(name = "AUTHOR")
public class Author extends AbstractEntity {

    @Column(name = "NAME")
    private String name;

    @JsonIgnoreProperties("author")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "AUTHOR_BOOK",
            joinColumns = @JoinColumn(name = "AUTHOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "BOOK_ID")
    )
    private List<Book> books;

    public void addBook(Book book) {
        if (null == this.books) {
            this.books = new ArrayList<>();
        }
        this.books.add(book);
        book.getAuthors().add(this);
    }
}