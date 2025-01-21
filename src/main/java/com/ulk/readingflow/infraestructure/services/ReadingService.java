package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.exceptions.UnauthorizedException;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.BookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.ReadingDTO;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.Reading;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.BookRepository;
import com.ulk.readingflow.infraestructure.repositories.ReadingRepository;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final BookRepository bookRepository;
    private final MessageUtils messagesUtils;

    @Autowired
    public ReadingService(ReadingRepository readingRepository, BookRepository bookRepository, MessageUtils messagesUtils) {
        this.readingRepository = readingRepository;
        this.bookRepository = bookRepository;
        this.messagesUtils = messagesUtils;
    }

    public List<ReadingDTO> listAllReadBooks(User user) {
        return readingRepository.findByUserIdOrderByReadingDateDesc(user.getId()).stream()
                .map(ReadingDTO::fromEntity)
                .toList();
    }

    public ReadingDTO save(ReadingDTO dto, User user) {
        Book book = findOrCreateBook(dto.getBook());
        Reading reading = Reading.builder()
                .readingDate(parseReadingDate(dto.getReadingDate()))
                .rating(dto.getRating())
                .user(user)
                .book(book)
                .build();

        Reading savedReading = readingRepository.save(reading);
        return ReadingDTO.fromEntity(savedReading);
    }

    public ReadingDTO update(ReadingDTO dto, User user) {
        UUID readingId = UUID.fromString(dto.getKey());
        Reading reading = readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dto.getKey())
                ));

        validateUserAccess(reading, user);

        reading.setReadingDate(parseReadingDate(dto.getReadingDate()));
        reading.setRating(dto.getRating());

        Book book = bookRepository.findBygDriveID(dto.getBook().getGDriveID())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dto.getBook().getGDriveID())
                ));
        reading.setBook(book);

        Reading updatedReading = readingRepository.save(reading);
        return ReadingDTO.fromEntity(updatedReading);
    }

    private Book findOrCreateBook(BookDTO bookDTO) {
        return bookRepository.findBygDriveID(bookDTO.getGDriveID())
                .orElseGet(() -> bookRepository.save(Book.builder()
                        .title(bookDTO.getTitle())
                        .description(bookDTO.getDescription())
                        .pages(bookDTO.getPages())
                        .gDriveID(bookDTO.getGDriveID())
                        .build())
                );
    }

    private void validateUserAccess(Reading reading, User user) {
        if (!reading.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    messagesUtils.getMessage(SystemMessages.UNAUTHORIZED, user.getId().toString())
            );
        }
    }

    private LocalDateTime parseReadingDate(String readingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        return LocalDateTime.parse(readingDate, formatter);
    }

    public Integer countReadings(User user) {
        return readingRepository.getReadingsCount(user.getId());
    }

    public Integer countPages(User user) {
        return readingRepository.getPagesCount(user.getId());
    }

    public boolean delete(UUID id, User user) {
        Reading reading = readingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, id.toString())
                ));

        validateUserAccess(reading, user);

        readingRepository.delete(reading);
        return true;
    }
}
