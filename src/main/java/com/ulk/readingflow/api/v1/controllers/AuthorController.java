package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.TopAuthorDTO;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.AuthorService;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/authors")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class AuthorController {

    private final AuthorService authorService;
    private final MessageUtils messageUtils;

    @Autowired
    public AuthorController(AuthorService authorService, MessageUtils messageUtils) {
        this.authorService = authorService;
        this.messageUtils = messageUtils;
    }

    @GetMapping("/top3")
    public ResponseEntity<SystemResponse<List<TopAuthorDTO>>> top3AuthorsWithMostBooks() {
        SystemResponse<List<TopAuthorDTO>> response = SystemResponse.<List<TopAuthorDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.authorService.top3AuthorsWithMostBooks())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/count")
    public ResponseEntity<SystemResponse<Integer>> countAuthors() {
        SystemResponse<Integer> response = SystemResponse.<Integer>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.authorService.countAuthors())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
