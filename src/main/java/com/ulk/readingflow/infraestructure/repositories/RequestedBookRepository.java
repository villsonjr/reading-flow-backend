package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.RequestedBook;
import com.ulk.readingflow.domain.enumerations.StatusEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestedBookRepository extends JpaRepository<RequestedBook, UUID> {

    /**
     * @Query("SELECT rb FROM RequestedBook rb WHERE LOWER(rb.title) = LOWER(:title) AND LOWER(rb.author) = LOWER(:author)")
     * Optional<RequestedBook> findByTitleAndAuthorIgnoreCase(@Param("title") String title, @Param("author") String author);
     */

    Optional<RequestedBook> findByTitleAndAuthorIgnoreCase(String title, String author);

    List<RequestedBook> findFirst3ByOwnerUserIdOrderByCreatedAtDesc(UUID userId);

    List<RequestedBook> findByOrderByCreatedAtDesc();

    void deleteByBook(Book book);

    @Query("SELECT rb FROM REQUESTED_BOOK rb " +
            "WHERE rb.status IN (:statuses) AND rb.ownerUser.id = :ownerId " +
            "ORDER BY rb.createdAt DESC")
    List<RequestedBook> findCreatedAndInProgress(@Param("statuses") List<StatusEnum> statuses,
                                                 @Param("ownerId") UUID ownerId);

    @Query("SELECT rb FROM REQUESTED_BOOK rb " +
            "WHERE rb.status IN (:statuses) AND rb.ownerUser.id = :ownerId " +
            "ORDER BY rb.createdAt DESC")
    List<RequestedBook> findLastThreeCanceledAndComplete(@Param("statuses") List<StatusEnum> statuses,
                                                         @Param("ownerId") UUID ownerId,
                                                         Pageable pageable);
}
