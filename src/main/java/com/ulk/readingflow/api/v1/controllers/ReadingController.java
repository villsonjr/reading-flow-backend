package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.ReadingDTO;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.ReadingService;
import com.ulk.readingflow.infraestructure.services.ReportPdfService;
import com.ulk.readingflow.utils.MessageUtils;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/readings")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class ReadingController {

    private final ReadingService readingService;
    private final MessageUtils messageUtils;
    private final ReportPdfService reportPdfService;

    @Autowired
    public ReadingController(ReadingService readingService, MessageUtils messageUtils, ReportPdfService reportPdfService) {
        this.readingService = readingService;
        this.messageUtils = messageUtils;
        this.reportPdfService = reportPdfService;
    }

    @GetMapping("/")
    public ResponseEntity<SystemResponse<List<ReadingDTO>>> listAllReadBooks(@AuthenticationPrincipal User user) {
        SystemResponse<List<ReadingDTO>> response = SystemResponse.<List<ReadingDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.listAllReadBooks(user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/readings-report")
    public ResponseEntity<byte[]> gerReadingsReport(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.reportPdfService.generatePdf("readingsReport", user));
    }

    @GetMapping("/count")
    public ResponseEntity<SystemResponse<Integer>> countReadings(@AuthenticationPrincipal User user) {
        SystemResponse<Integer> response = SystemResponse.<Integer>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.countReadings(user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/countPages")
    public ResponseEntity<SystemResponse<Integer>> countPages(@AuthenticationPrincipal User user) {
        SystemResponse<Integer> response = SystemResponse.<Integer>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.countPages(user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<SystemResponse<ReadingDTO>> create(@RequestBody ReadingDTO dto, @AuthenticationPrincipal User user) {
        SystemResponse<ReadingDTO> response = SystemResponse.<ReadingDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.save(dto, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/")
    public ResponseEntity<SystemResponse<ReadingDTO>> update(@RequestBody ReadingDTO dto, @AuthenticationPrincipal User user) {
        SystemResponse<ReadingDTO> response = SystemResponse.<ReadingDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.update(dto, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SystemResponse<Boolean>> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        SystemResponse<Boolean> response = SystemResponse.<Boolean>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.readingService.delete(id, user))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
