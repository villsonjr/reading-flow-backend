package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Optional<Book> findByTitle(String bookTitle);

    Optional<Book> findBygDriveID(String gDriveID);

    @Query("SELECT COUNT(ID) FROM BOOK")
    Integer getCountBooks();

    List<Book> findByGenre(Genre genre);
}
