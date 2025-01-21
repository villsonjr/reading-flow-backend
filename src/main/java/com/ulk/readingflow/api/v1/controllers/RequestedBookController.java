package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.requests.dtos.RequestBookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.RequestedBookDTO;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.RequestedBookService;
import com.ulk.readingflow.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.util.UUID;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/book-requests")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class RequestedBookController {

    private final RequestedBookService requestedBookService;
    private final MessageUtils messageUtils;

    @Autowired
    public RequestedBookController(RequestedBookService requestedBookService, MessageUtils messageUtils) {
        this.requestedBookService = requestedBookService;
        this.messageUtils = messageUtils;
    }

    @GetMapping("/")
    public ResponseEntity<SystemResponse<List<RequestedBookDTO>>> listAllRequestedBooks() {
        SystemResponse<List<RequestedBookDTO>> response = SystemResponse.<List<RequestedBookDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.listAllRequestedBooks())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/last3")
    public ResponseEntity<SystemResponse<List<RequestedBookDTO>>> last3RequestedBooks(@AuthenticationPrincipal User user) {
        SystemResponse<List<RequestedBookDTO>> response = SystemResponse.<List<RequestedBookDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.last3RequestedBooks(user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/lastRequests")
    public ResponseEntity<SystemResponse<List<RequestedBookDTO>>> lastRequests(@AuthenticationPrincipal User user) {
        SystemResponse<List<RequestedBookDTO>> response = SystemResponse.<List<RequestedBookDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.lastRequestedBooks(user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/book")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> getBookByIsbn(@RequestBody RequestedBookDTO dto) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.getRequestedBookByIsbn(dto))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> create(
            @RequestBody RequestBookDTO dto,
            @AuthenticationPrincipal User user,
            HttpServletRequest request
    ) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.create(dto, user, request))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> uploadRequest(
            @RequestParam("epubFile") MultipartFile epubFile,
            @RequestParam("requestedBookDTO") String dtoRequestedBookJSON,
            @RequestParam("sendMail") Boolean sendMail,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.uploadRequest(epubFile, dtoRequestedBookJSON, sendMail, authenticatedUser))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> update(@RequestBody RequestedBookDTO dto, @AuthenticationPrincipal User user) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.update(dto, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/assign")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> assign(@RequestBody RequestedBookDTO dto, @AuthenticationPrincipal User user) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.assign(dto, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/cancel")
    public ResponseEntity<SystemResponse<RequestedBookDTO>> cancel(@RequestBody RequestedBookDTO dto, @AuthenticationPrincipal User user) {
        SystemResponse<RequestedBookDTO> response = SystemResponse.<RequestedBookDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.cancel(dto, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SystemResponse<Boolean>> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        SystemResponse<Boolean> response = SystemResponse.<Boolean>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.requestedBookService.delete(id, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
