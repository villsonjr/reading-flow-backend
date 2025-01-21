package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {

    Optional<Genre> findByName(String name);

    List<Genre> findAllByOrderByNameAsc();
}
