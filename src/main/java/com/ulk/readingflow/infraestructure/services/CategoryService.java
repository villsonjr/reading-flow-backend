package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.domain.entities.Category;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.CategoryRepository;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MessageUtils messagesUtils;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, MessageUtils messagesUtils) {
        this.categoryRepository = categoryRepository;
        this.messagesUtils = messagesUtils;
    }

    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    public Optional<Category> getCategoryByID(UUID uuid) {
        return this.categoryRepository.findById(uuid);
    }

    public Optional<Category> getCategoryByName(String name) {
        return this.categoryRepository.findByName(name);
    }

    public Category addCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public Category updateCategory(UUID uuid, Category updatedCategory) {
        Category category = this.categoryRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, uuid.toString())
                ));

        category.setName(updatedCategory.getName());
        return this.categoryRepository.save(category);
    }
}
