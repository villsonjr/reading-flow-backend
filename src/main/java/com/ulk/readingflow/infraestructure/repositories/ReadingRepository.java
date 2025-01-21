package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, UUID> {

    @Query("SELECT COUNT(r.id) FROM READING r WHERE r.user.id = :userID")
    Integer getReadingsCount(UUID userID);

    @Query("SELECT SUM(b.pages) FROM READING r INNER JOIN BOOK b ON r.book.id = b.id where r.user.id = :userID")
    Integer getPagesCount(UUID userID);

    List<Reading> findByUserIdOrderByReadingDateDesc(UUID userID);

    void deleteByBook(Book book);
}
