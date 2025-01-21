package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);
}
