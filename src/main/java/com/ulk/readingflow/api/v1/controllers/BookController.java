package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.BookDTO;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.BookService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/books")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class BookController {

    private final BookService bookService;
    private final MessageUtils messageUtils;

    @Autowired
    public BookController(BookService bookService, MessageUtils messageUtils) {
        this.bookService = bookService;
        this.messageUtils = messageUtils;
    }

    @GetMapping("/")
    public ResponseEntity<SystemResponse<List<BookDTO>>> listAllBooks() {
        SystemResponse<List<BookDTO>> response = SystemResponse.<List<BookDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.bookService.listAllBooks())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/count")
    public ResponseEntity<SystemResponse<Integer>> countBooks() {
        SystemResponse<Integer> response = SystemResponse.<Integer>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.bookService.countBooks())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<SystemResponse<BookDTO>> create(
            @RequestParam("epubFile") MultipartFile epubFile,
            @RequestParam("dtoBookJSON") String dtoBookJSON
    ) {
        SystemResponse<BookDTO> response = SystemResponse.<BookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.bookService.create(epubFile, dtoBookJSON))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/")
    public ResponseEntity<SystemResponse<BookDTO>> update(@RequestBody BookDTO dto) {
        Book bookUpdated = this.bookService.update(dto);
        SystemResponse<BookDTO> response = SystemResponse.<BookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(BookDTO.fromEntity(bookUpdated))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{gDriveID}")
    public ResponseEntity<SystemResponse<Boolean>> delete(@PathVariable String gDriveID) {
        SystemResponse<Boolean> response = SystemResponse.<Boolean>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.bookService.delete(gDriveID))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
