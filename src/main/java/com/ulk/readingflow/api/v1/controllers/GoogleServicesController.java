package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.BookDTO;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.GoogleService;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/google")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class GoogleServicesController {

    private final GoogleService googleService;
    private final MessageUtils messageUtils;

    @Autowired
    public GoogleServicesController(GoogleService googleService, MessageUtils messageUtils) {
        this.googleService = googleService;
        this.messageUtils = messageUtils;
    }

    @GetMapping("/isbn")
    public ResponseEntity<SystemResponse<BookDTO>> requestMethodName(@RequestParam String isbn) {
        Book book = this.googleService.getBookData(isbn);
        SystemResponse<BookDTO> response = SystemResponse.<BookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(BookDTO.fromEntity(book))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
