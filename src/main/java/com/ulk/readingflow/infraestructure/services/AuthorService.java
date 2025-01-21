package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.v1.payloads.responses.dtos.TopAuthorDTO;
import com.ulk.readingflow.domain.entities.transactionals.TopAuthor;
import com.ulk.readingflow.infraestructure.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<TopAuthorDTO> top3AuthorsWithMostBooks() {
        return authorRepository.top3AuthorsWithMostBooks()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private TopAuthorDTO convertToDTO(TopAuthor topAuthor) {
        return TopAuthorDTO.builder()
                .name(topAuthor.getName())
                .bookCount(topAuthor.getBookCount())
                .build();
    }

    public Integer countAuthors() {
        return authorRepository.countAuthors();
    }
}
