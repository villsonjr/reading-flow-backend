package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Author;
import com.ulk.readingflow.domain.entities.transactionals.TopAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {

    Optional<Author> findByName(String authorName);

    @Query(value = "SELECT a.name, COUNT(ab.book_id) AS bookCount " +
            "FROM AUTHORS a " +
            "JOIN AUTHOR_BOOK ab ON a.id = ab.author_id " +
            "GROUP BY a.id " +
            "ORDER BY bookCount DESC " +
            "LIMIT 3", nativeQuery = true)
    List<TopAuthor> top3AuthorsWithMostBooks();

    @Query("SELECT COUNT(ID) FROM AUTHOR")
    Integer countAuthors();
}
