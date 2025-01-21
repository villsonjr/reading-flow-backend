package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.GenreDTO;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.GenreService;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/genres")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class GenreController {

    private final GenreService genreService;
    private final MessageUtils messageUtils;

    @Autowired
    public GenreController(GenreService genreService, MessageUtils messageUtils) {
        this.genreService = genreService;
        this.messageUtils = messageUtils;
    }

    @GetMapping("/")
    public ResponseEntity<SystemResponse<List<GenreDTO>>> listGenres() {
        SystemResponse<List<GenreDTO>> response = SystemResponse.<List<GenreDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.genreService.getAllGenres())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<SystemResponse<GenreDTO>> create(@RequestBody GenreDTO dto) {
        SystemResponse<GenreDTO> response = SystemResponse.<GenreDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.genreService.create(dto))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/")
    public ResponseEntity<SystemResponse<GenreDTO>> updateCategories(@RequestBody GenreDTO dto) {
        SystemResponse<GenreDTO> response = SystemResponse.<GenreDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.genreService.updateCategories(dto))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{genreName}")
    public ResponseEntity<SystemResponse<Boolean>> delete(@PathVariable String genreName) {
        SystemResponse<Boolean> response = SystemResponse.<Boolean>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.genreService.delete(genreName))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
