package com.ulk.readingflow.infraestructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulk.readingflow.api.exceptions.JsonException;
import com.ulk.readingflow.api.exceptions.ResourceAlreadyExistsException;
import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.AuthorDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.BookDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.CategoryDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.RequestedBookDTO;
import com.ulk.readingflow.domain.entities.Author;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.entities.Category;
import com.ulk.readingflow.domain.entities.Genre;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.AuthorRepository;
import com.ulk.readingflow.infraestructure.repositories.BookRepository;
import com.ulk.readingflow.infraestructure.repositories.CategoryRepository;
import com.ulk.readingflow.infraestructure.repositories.GenreRepository;
import com.ulk.readingflow.infraestructure.repositories.ReadingRepository;
import com.ulk.readingflow.infraestructure.repositories.RequestedBookRepository;
import com.ulk.readingflow.utils.MessageUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ReadingRepository readingRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final GoogleService googleService;
    private final CategoryRepository categoryRepository;
    private final MessageUtils messagesUtils;
    private final RequestedBookRepository requestedBookRepository;

    @Autowired
    public BookService(BookRepository bookRepository, ReadingRepository readingRepository,
                       GenreRepository genreRepository, AuthorRepository authorRepository,
                       GoogleService googleService, CategoryRepository categoryRepository,
                       MessageUtils messagesUtils, RequestedBookRepository requestedBookRepository
    ) {
        this.bookRepository = bookRepository;
        this.readingRepository = readingRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.googleService = googleService;
        this.categoryRepository = categoryRepository;
        this.messagesUtils = messagesUtils;
        this.requestedBookRepository = requestedBookRepository;
    }

    public List<BookDTO> listAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookDTO::fromEntity)
                .toList();
    }

    public Book update(BookDTO dto) {
        Book book = findBookByGDriveID(dto.getGDriveID());
        updateAuthors(book, dto.getAuthors());
        updateGenresAndCategories(book, dto);
        return bookRepository.save(book);
    }

    private Book findBookByGDriveID(String gDriveID) {
        return bookRepository.findBygDriveID(gDriveID).orElseThrow(() ->
                new ResourceNotFoundException(messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, gDriveID)));
    }

    private void updateAuthors(Book book, List<AuthorDTO> authorDTOs) {
        List<Author> updatedAuthors = authorDTOs.stream()
                .map(this::findOrCreateAuthor)
                .toList();

        Set<Author> authorsToRemove = new HashSet<>(book.getAuthors());
        updatedAuthors.forEach(authorsToRemove::remove);
        removeAuthorsFromBook(book, authorsToRemove);

        updatedAuthors.forEach(author -> {
            if (!book.getAuthors().contains(author)) {
                book.addAuthor(author);
            }
        });

        book.getAuthors().forEach(author -> {
            if (!author.getBooks().contains(book)) {
                author.addBook(book);
            }
        });
    }

    private Author findOrCreateAuthor(AuthorDTO authorDTO) {
        return authorRepository.findByName(authorDTO.getName())
                .orElseGet(() -> authorRepository.save(Author.builder()
                        .name(authorDTO.getName())
                        .books(new ArrayList<>()).build()));
    }

    private void removeAuthorsFromBook(Book book, Set<Author> authorsToRemove) {
        authorsToRemove.forEach(author -> {
            book.removeAuthor(author);
            if (author.getBooks().isEmpty()) {
                authorRepository.delete(author);
            }
        });
    }

    private void updateGenresAndCategories(Book book, BookDTO dto) {
        Genre genre = genreRepository.findByName(dto.getGenre().getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dto.getGenre().getName())));

        book.setTitle(dto.getTitle());
        book.setGenre(genre);
        book.setDescription(dto.getDescription());
        book.setPages(dto.getPages());

        Set<Category> updatedCategories = dto.getCategories().stream()
                .map(this::findOrCreateCategory)
                .collect(Collectors.toSet());

        Set<Category> categoriesToRemove = new HashSet<>(book.getCategories());
        categoriesToRemove.removeAll(updatedCategories);
        book.getCategories().removeAll(categoriesToRemove);

        updatedCategories.forEach(category -> {
            if (!book.getCategories().contains(category)) {
                book.getCategories().add(category);
            }
        });
    }

    private Category findOrCreateCategory(CategoryDTO categoryDTO) {
        return categoryRepository.findByName(categoryDTO.getName())
                .orElseGet(() -> categoryRepository.save(new Category(categoryDTO.getName())));
    }

    public Integer countBooks() {
        return bookRepository.getCountBooks();
    }

    @Transactional
    public boolean delete(String gDriveID) {
        Book book = findBookByGDriveID(gDriveID);
        removeBookFromAuthors(book);
        readingRepository.deleteByBook(book);
        requestedBookRepository.deleteByBook(book);
        bookRepository.delete(book);
        return true;
    }

    private void removeBookFromAuthors(Book book) {
        book.getAuthors().forEach(author -> author.getBooks().remove(book));
    }

    public BookDTO create(MultipartFile epubFile, String dtoBookJSON) {
        try {
            BookDTO dto = new ObjectMapper().readValue(dtoBookJSON, BookDTO.class);
            ensureBookDoesNotExist(dto.getTitle());

            List<Author> authors = dto.getAuthors().stream()
                    .map(this::findOrCreateAuthor)
                    .toList();

            Genre genre = genreRepository.findByName(dto.getGenre().getName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dto.getGenre().getName())));

            Set<Category> categories = dto.getCategories().stream()
                    .map(this::findOrCreateCategory)
                    .collect(Collectors.toSet());

            String bookGdID = googleService.uploadFile(epubFile, dto.getTitle(), dto.getAuthorsReport());
            Book book = createBook(dto, authors, genre, categories, bookGdID);

            book = bookRepository.save(book);
            updateAuthorBooks(authors, book);

            return BookDTO.fromEntity(book);
        } catch (JsonProcessingException e) {
            throw new JsonException(messagesUtils.getMessage(SystemMessages.ERROR_PARSE_JSON, e.getMessage()));
        }
    }

    private void ensureBookDoesNotExist(String title) {
        bookRepository.findByTitle(title).ifPresent(book -> {
            throw new ResourceAlreadyExistsException(messagesUtils.getMessage(SystemMessages.RESOURCE_ALREADY_EXISTS, title));
        });
    }

    private Book createBook(BookDTO dto, List<Author> authors, Genre genre, Set<Category> categories, String gDriveID) {
        return Book.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .pages(dto.getPages())
                .authors(authors)
                .gDriveID(gDriveID)
                .isbn(dto.getIsbn())
                .genre(genre)
                .categories(categories)
                .build();
    }

    private void updateAuthorBooks(List<Author> authors, Book book) {
        authors.forEach(author -> {
            author.getBooks().add(book);
            authorRepository.save(author);
        });
    }

    public Book create(RequestedBookDTO dtoRequestedBook, String bookGdID) {
        List<AuthorDTO> authorDTOs = Arrays.stream(dtoRequestedBook.getAuthorName().split(", | e "))
                .map(AuthorDTO::new)
                .toList();

        List<Author> authors = authorDTOs.stream()
                .map(this::findOrCreateAuthor)
                .toList();

        Genre genre = genreRepository.findByName(dtoRequestedBook.getBook().getGenre().getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, dtoRequestedBook.getBook().getGenre().getName())));

        Set<Category> categories = dtoRequestedBook.getBook().getCategories().stream()
                .map(this::findOrCreateCategory)
                .collect(Collectors.toSet());

        Book book = createBook(dtoRequestedBook.getBook(), authors, genre, categories, bookGdID);
        return bookRepository.save(book);
    }
}
