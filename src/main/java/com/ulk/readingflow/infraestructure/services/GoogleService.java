package com.ulk.readingflow.infraestructure.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.ulk.readingflow.api.exceptions.ResourceAlreadyExistsException;
import com.ulk.readingflow.api.exceptions.UploadFileException;
import com.ulk.readingflow.api.v1.payloads.responses.GoogleBooksApiResponse;
import com.ulk.readingflow.core.SecretProperties;
import com.ulk.readingflow.domain.entities.Author;
import com.ulk.readingflow.domain.entities.Book;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.AuthorRepository;
import com.ulk.readingflow.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ulk.readingflow.domain.constants.SystemConstants.GOOGLE_CREDENTIALS_FILEPATH;


@Slf4j
@Service
public class GoogleService {


    private SecretProperties secretProperties;

    private final MessageUtils messagesUtils;
    private final AuthorRepository authorRepository;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${application.gdrive_folder_id}")
    private String gDriveFolderID;

    @Value("${application.name}")
    private String applicationName;

    @Autowired
    public GoogleService(SecretProperties secretProperties, AuthorRepository authorRepository, MessageUtils messagesUtils) {
        this.secretProperties = secretProperties;
        this.authorRepository = authorRepository;
        this.messagesUtils = messagesUtils;
    }

    public Book getBookData(String isbn) {
        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&key=" + secretProperties.getGoogleSecretKey();
        GoogleBooksApiResponse response = new RestTemplate().getForObject(apiUrl, GoogleBooksApiResponse.class);

        if (response != null && response.getTotalItems() > 0) {
            GoogleBooksApiResponse.Item item = response.getItems().get(0);
            List<Author> authors = processAuthors(item.getVolumeInfo().getAuthors());

            return Book.builder()
                    .title(item.getVolumeInfo().getTitle())
                    .description(item.getVolumeInfo().getDescription())
                    .pages(item.getVolumeInfo().getPageCount())
                    .authors(authors)
                    .build();
        }
        return new Book();
    }

    private List<Author> processAuthors(List<String> googleAuthors) {
        List<Author> authors = this.authorRepository.findAll();
        List<Author> authorsToAdd = googleAuthors.stream()
                .map(googleAuthor -> authors.stream()
                        .filter(a -> a.getName().equalsIgnoreCase(googleAuthor))
                        .findFirst()
                        .orElseGet(() -> {
                            Author newAuthor = new Author();
                            newAuthor.setName(googleAuthor);
                            return newAuthor;
                        }))
                .toList();

        List<Author> newAuthors = authorsToAdd.stream()
                .filter(a -> a.getId() == null)
                .toList();

        if (!newAuthors.isEmpty()) {
            this.authorRepository.saveAll(newAuthors);
        }

        return authorsToAdd;
    }

    public void listFiles() throws GeneralSecurityException, IOException {
        Drive drive = getDriveService();

        FileList result = drive.files().list()
                .setQ("'" + this.gDriveFolderID + "' in parents")
                .setFields("files(id, name)")
                .execute();

        result.getFiles().forEach(file ->
                log.info("Nome: {} \t|| \tID: {}", file.getName(), file.getId())
        );

        log.info("Total de arquivos: {}", result.getFiles().size());
    }

    public String uploadFile(MultipartFile epubFile, String title, String authors) {
        try {
            if (findByName(epubFile.getOriginalFilename()).isPresent()) {
                throw new ResourceAlreadyExistsException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_ALREADY_EXISTS, epubFile.getOriginalFilename()));
            }

            java.io.File tempFile = java.io.File.createTempFile("upload", epubFile.getOriginalFilename());
            File fileMetaData = new File();
            fileMetaData.setName(title + " - " + authors + ".epub");
            fileMetaData.setParents(Collections.singletonList(this.gDriveFolderID));

            epubFile.transferTo(tempFile);

            Drive drive = getDriveService();
            FileContent mediaContent = new FileContent(epubFile.getContentType(), tempFile);

            File file = drive.files().create(fileMetaData, mediaContent)
                    .setFields("id")
                    .execute();

            tempFile.delete();
            return file.getId();
        } catch (IOException | GeneralSecurityException e) {
            throw new UploadFileException(e.getMessage());
        }
    }

    private Optional<File> findByName(String originalFilename) throws IOException, GeneralSecurityException {
        Drive drive = getDriveService();

        FileList result = drive.files().list()
                .setQ("'" + this.gDriveFolderID + "' in parents and trashed = false and name = '" + originalFilename + "'")
                .setFields("files(id, name)")
                .execute();

        return result.getFiles().stream().findFirst();
    }

    private Drive getDriveService() throws GeneralSecurityException, IOException {
        InputStream in = GoogleService.class.getResourceAsStream(GOOGLE_CREDENTIALS_FILEPATH);
        if (in == null) {
            throw new RuntimeException("Arquivo de credenciais não encontrado no classpath");
        }

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(in)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName("Google Drive API Java Quickstart")
                .build();
    }
}
