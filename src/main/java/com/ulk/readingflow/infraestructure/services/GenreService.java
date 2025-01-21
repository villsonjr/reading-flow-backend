package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.GenreDTO;
import com.ulk.readingflow.domain.entities.Category;
import com.ulk.readingflow.domain.entities.Genre;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.BookRepository;
import com.ulk.readingflow.infraestructure.repositories.GenreRepository;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final CategoryService categoryService;
    private final BookRepository bookRepository;
    private final MessageUtils messagesUtils;

    @Autowired
    public GenreService(GenreRepository genreRepository, CategoryService categoryService, BookRepository bookRepository, MessageUtils messagesUtils) {
        this.genreRepository = genreRepository;
        this.categoryService = categoryService;
        this.bookRepository = bookRepository;
        this.messagesUtils = messagesUtils;
    }

    public List<GenreDTO> getAllGenres() {
        return this.genreRepository.findAllByOrderByNameAsc().stream()
                .map(GenreDTO::fromEntity)
                .toList();
    }

    public Optional<Genre> getGenreByID(UUID uuid) {
        return this.genreRepository.findById(uuid);
    }

    public GenreDTO create(GenreDTO genreDTO) {
        Genre genre = this.genreRepository.findByName(genreDTO.getName())
                .orElseGet(() -> {
                    Genre newGenre = Genre.builder()
                            .name(genreDTO.getName())
                            .icon(genreDTO.getIcon())
                            .categories(genreDTO.getCategories().stream()
                                    .map(categoryName -> categoryService.getCategoryByName(categoryName)
                                            .orElseGet(() -> this.categoryService.addCategory(new Category(categoryName))))
                                    .collect(Collectors.toSet()))
                            .build();
                    return this.genreRepository.save(newGenre);
                });

        return GenreDTO.fromEntity(genre);
    }

    public GenreDTO updateGenre(UUID uuid, GenreDTO genreDTO) {
        Genre genre = this.genreRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, uuid.toString())
                ));

        genre.setName(genreDTO.getName());
        genre.setIcon(genreDTO.getIcon());
        genre.setCategories(genreDTO.getCategories().stream()
                .map(name -> categoryService.getCategoryByName(name)
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(name);
                            return this.categoryService.addCategory(newCategory);
                        }))
                .collect(Collectors.toSet()));

        return GenreDTO.fromEntity(this.genreRepository.save(genre));
    }

    public void removeGenre(UUID uuid) {
        Genre genre = this.genreRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, uuid.toString())
                ));

        this.bookRepository.findByGenre(genre).forEach(book -> {
            book.setGenre(null);
            book.setCategories(null);
            this.bookRepository.save(book);
        });

        this.genreRepository.delete(genre);
    }

    public GenreDTO updateCategories(GenreDTO dto) {
        Genre genre = this.genreRepository.findByName(dto.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dto.getName())
                ));

        Set<Category> updatedCategories = dto.getCategories().stream()
                .map(name -> categoryService.getCategoryByName(name)
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(name);
                            return this.categoryService.addCategory(newCategory);
                        }))
                .collect(Collectors.toSet());

        genre.setCategories(updatedCategories);
        genre.setIcon(dto.getIcon());
        return GenreDTO.fromEntity(this.genreRepository.save(genre));
    }

    public Boolean delete(String genreName) {
        Genre genre = this.genreRepository.findByName(genreName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, genreName)
                ));

        this.bookRepository.findByGenre(genre).forEach(book -> {
            book.setGenre(null);
            book.setCategories(null);
            this.bookRepository.save(book);
        });

        this.genreRepository.delete(genre);
        return true;
    }
}
