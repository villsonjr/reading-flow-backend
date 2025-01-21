package com.ulk.readingflow.infraestructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulk.readingflow.api.exceptions.JsonException;
import com.ulk.readingflow.api.exceptions.ResourceAlreadyExistsException;
import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.exceptions.UnauthorizedException;
import com.ulk.readingflow.api.v1.payloads.requests.dtos.RequestBookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.BookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.RequestedBookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserPreferenceDTO;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.RequestedBook;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.entities.UserPreferences;
import com.ulk.readingflow.domain.enumerations.StatusEnum;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.RequestedBookRepository;
import com.ulk.readingflow.infraestructure.repositories.UserRepository;
import com.ulk.readingflow.infraestructure.services.mail.EmailService;
import com.ulk.readingflow.utils.MessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RequestedBookService {

    private final RequestedBookRepository requestedBookRepository;
    private final BookService bookService;
    private final GoogleService googleService;
    private final UserRepository userRepository;
    private final EmailService mailService;
    private final MessageUtils messagesUtils;
    private final ObjectMapper objectMapper;

    @Autowired
    public RequestedBookService(RequestedBookRepository requestedBookRepository, BookService bookService,
                                GoogleService googleService, UserRepository userRepository,
                                EmailService mailService, MessageUtils messagesUtils,
                                ObjectMapper objectMapper) {
        this.requestedBookRepository = requestedBookRepository;
        this.bookService = bookService;
        this.googleService = googleService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.messagesUtils = messagesUtils;
        this.objectMapper = objectMapper;
    }

    public RequestedBookDTO create(RequestBookDTO dto, User user, HttpServletRequest request) {
        requestedBookRepository.findByTitleAndAuthorIgnoreCase(dto.getBookTitle(), dto.getAuthorName())
                .ifPresent(existingRequestedBook -> {
                    String date = existingRequestedBook.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss"));
                    throw new ResourceAlreadyExistsException("O livro '" + dto.getBookTitle() + "' do(a) autor(a) '"
                            + dto.getAuthorName() + "' já foi solicitado anteriormente em: " + date);
                });

        RequestedBook newRequestedBook = RequestedBook.builder()
                .title(dto.getBookTitle())
                .author(dto.getAuthorName())
                .ownerUser(user)
                .userIp(request.getRemoteAddr())
                .status(StatusEnum.CREATED)
                .build();

        RequestedBook savedRequestedBook = requestedBookRepository.save(newRequestedBook);

        Boolean notifyByMail = user.getPreferences().stream().filter(pref -> "notifyByMail".equals(pref.getKey()))
                .map(UserPreferences::getValue)
                .findFirst()
                .map(Boolean::parseBoolean)
                .orElse(Boolean.FALSE);

        if (Boolean.TRUE.equals(notifyByMail)) {
            mailService.sendMail(RequestedBookDTO.fromEntity(savedRequestedBook), null);
        }

        return RequestedBookDTO.fromEntity(savedRequestedBook);
    }

    public List<RequestedBookDTO> listAllRequestedBooks() {
        return requestedBookRepository.findByOrderByCreatedAtDesc().stream()
                .map(RequestedBookDTO::fromEntity)
                .toList();
    }

    public List<RequestedBookDTO> last3RequestedBooks(User user) {
        return requestedBookRepository.findFirst3ByOwnerUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(RequestedBookDTO::fromEntity)
                .toList();
    }

    public List<RequestedBookDTO> lastRequestedBooks(User user) {
        List<StatusEnum> createdAndInProgressStatuses = Arrays.asList(StatusEnum.CREATED, StatusEnum.IN_PROGRESS);
        List<StatusEnum> canceledAndCompleteStatuses = Arrays.asList(StatusEnum.CANCELED, StatusEnum.COMPLETE);
        List<RequestedBook> createdAndInProgress = requestedBookRepository.findCreatedAndInProgress(createdAndInProgressStatuses, user.getId());

        Pageable topThree = PageRequest.of(0, 3);
        List<RequestedBook> lastThreeCanceled = requestedBookRepository.findLastThreeCanceledAndComplete(canceledAndCompleteStatuses, user.getId(), topThree);
        List<RequestedBook> lastThreeComplete = requestedBookRepository.findLastThreeCanceledAndComplete(canceledAndCompleteStatuses, user.getId(), topThree);

        List<RequestedBook> result = new ArrayList<>();
        result.addAll(createdAndInProgress);
        result.addAll(lastThreeCanceled);
        result.addAll(lastThreeComplete);

        result.sort(Comparator.comparing(RequestedBook::getCreatedAt).reversed());

        return result.stream().map(RequestedBookDTO::fromEntity).toList();

    }

    public RequestedBookDTO update(RequestedBookDTO dto, User user) {
        RequestedBook requestedBook = findRequestedBookById(dto.getKey());
        validateUserAccess(requestedBook, user);

        requestedBook.setTitle(dto.getBookTitle());
        requestedBook.setAuthor(dto.getAuthorName());
        RequestedBook updatedRequestedBook = requestedBookRepository.save(requestedBook);

        return RequestedBookDTO.fromEntity(updatedRequestedBook);
    }

    public RequestedBookDTO cancel(RequestedBookDTO dto, User user) {
        RequestedBook requestedBook = findRequestedBookById(dto.getKey());
        validateUserAccess(requestedBook, user);
        requestedBook.setStatus(StatusEnum.CANCELED);
        RequestedBook canceledRequestedBook = requestedBookRepository.save(requestedBook);
        return RequestedBookDTO.fromEntity(canceledRequestedBook);
    }

    public boolean delete(UUID id, User user) {
        RequestedBook requestedBook = findRequestedBookById(id.toString());
        validateUserAccess(requestedBook, user);
        requestedBookRepository.delete(requestedBook);
        return true;
    }

    public RequestedBookDTO assign(RequestedBookDTO dto, User responsible) {
        RequestedBook requestedBook = findRequestedBookById(dto.getKey());
        User assignedUser = findUserByUsername(responsible.getUsername());

        requestedBook.setAssignedUser(assignedUser);
        requestedBook.setStartedAt(LocalDateTime.now());
        requestedBook.setStatus(StatusEnum.IN_PROGRESS);

        RequestedBook updatedRequestedBook = requestedBookRepository.save(requestedBook);

        return sendMail(dto, updatedRequestedBook);
    }

    private RequestedBookDTO sendMail(RequestedBookDTO dto, RequestedBook updatedRequestedBook) {
        Boolean notifyByMail = dto.getOwner().getPreferences().stream().filter(pref -> "notifyByMail".equals(pref.getKey()))
                .map(UserPreferenceDTO::getValue)
                .findFirst()
                .map(Boolean::parseBoolean)
                .orElse(Boolean.FALSE);

        if (Boolean.TRUE.equals(notifyByMail)) {
            mailService.sendMail(RequestedBookDTO.fromEntity(updatedRequestedBook), null);
        }

        return RequestedBookDTO.fromEntity(updatedRequestedBook);
    }

    public RequestedBookDTO getRequestedBookByIsbn(RequestedBookDTO dto) {
        Book book = googleService.getBookData(dto.getBook().getIsbn());
        dto.setBook(BookDTO.fromEntity(book));
        return dto;
    }

    public RequestedBookDTO uploadRequest(MultipartFile epubFile, String dtoRequestedBookJSON, Boolean sendMail, User authenticatedUser) {
        try {
            RequestedBookDTO dtoRequestedBook = objectMapper.readValue(dtoRequestedBookJSON, RequestedBookDTO.class);

            RequestedBook requestedBook = findRequestedBookById(dtoRequestedBook.getKey());
            validateAssignedUser(requestedBook, authenticatedUser);

            String bookGdID = googleService.uploadFile(epubFile, dtoRequestedBook.getBook().getTitle(), dtoRequestedBook.getBook().getAuthorsReport());
            requestedBook.setBook(bookService.create(dtoRequestedBook, bookGdID));
            requestedBook.setStatus(StatusEnum.COMPLETE);
            requestedBook.setClosedAt(LocalDateTime.now());
            RequestedBook updatedRequestedBook = requestedBookRepository.save(requestedBook);

            mailService.sendMail(RequestedBookDTO.fromEntity(updatedRequestedBook), epubFile);

            return sendMail(dtoRequestedBook, updatedRequestedBook);
        } catch (JsonProcessingException e) {
            throw new JsonException(messagesUtils.getMessage(SystemMessages.ERROR_PARSE_JSON, e.getMessage()));
        }
    }

    private RequestedBook findRequestedBookById(String id) {
        return requestedBookRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, id)
                ));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, username)
                ));
    }

    private void validateUserAccess(RequestedBook requestedBook, User user) {
        if (!requestedBook.getOwnerUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    messagesUtils.getMessage(SystemMessages.UNAUTHORIZED, user.getId().toString())
            );
        }
    }

    private void validateAssignedUser(RequestedBook requestedBook, User user) {
        if (!requestedBook.getAssignedUser().equals(user)) {
            throw new UnauthorizedException(
                    messagesUtils.getMessage(SystemMessages.UNAUTHORIZED)
            );
        }
    }
}
