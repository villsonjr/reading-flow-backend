package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.exceptions.InvalidFileException;
import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.exceptions.UploadFileException;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserDTO;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserPreferenceDTO;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.entities.UserPreferences;
import com.ulk.readingflow.domain.enumerations.StatusEnum;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.UserRepository;
import com.ulk.readingflow.utils.MessageUtils;
import com.ulk.readingflow.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MessageUtils messagesUtils;

    @Autowired
    public UserService(UserRepository userRepository, MessageUtils messagesUtils) {
        this.userRepository = userRepository;
        this.messagesUtils = messagesUtils;
    }

    @Value("${application.images.path}")
    private String imagesPath;

    public List<UserDTO> listAllUsers() {
        return this.userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    public User create(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException(this.messagesUtils.getMessage(SystemMessages.BAD_CREDENTIALS)));
    }

    public boolean isRegistered(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, email)));
    }

    public UserDTO update(UserDTO userDTO) {
        User uDB = this.userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, userDTO.getUsername())));

        SystemUtils.copyNonNullProperties(userDTO, uDB);

        LocalDate localDate = Instant.parse(userDTO.getBirthday()).atZone(ZoneId.systemDefault()).toLocalDate();
        uDB.setBirthday(localDate);

        if (userDTO.getPreferences() != null) {
            Map<String, UserPreferenceDTO> dtoPreferencesMap = userDTO.getPreferences().stream()
                    .collect(Collectors.toMap(UserPreferenceDTO::getKey, prefDTO -> prefDTO));

            uDB.getPreferences().forEach(existingPref -> {
                UserPreferenceDTO dtoPref = dtoPreferencesMap.get(existingPref.getKey());
                if (dtoPref != null) {
                    existingPref.setValue(dtoPref.getValue());
                }
            });

            dtoPreferencesMap.forEach((key, dtoPref) -> {
                if (uDB.getPreferences().stream().noneMatch(p -> p.getKey().equals(key))) {
                    uDB.getPreferences().add(
                            UserPreferences.builder()
                                    .key(dtoPref.getKey())
                                    .value(dtoPref.getValue())
                                    .user(uDB)
                                    .build()
                    );
                }
            });
        }

        return UserDTO.fromEntity(this.userRepository.save(uDB));
    }


    public boolean delete(UUID id) {
        User uDB = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, id.toString())));

        this.userRepository.delete(uDB);
        return true;
    }

    public boolean blockUser(UUID userID) {
        User uDB = this.userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, userID.toString())));

        uDB.setStatus(StatusEnum.BLOCKED);
        this.userRepository.save(uDB);
        return true;
    }

    public String uploadProfileImage(String username, MultipartFile imageFile) {
        validateImage(imageFile);

        User uDB = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, username)));

        try {
            String fileName = generateUniqueFileName(imageFile.getOriginalFilename());
            Path userFolderPath = Paths.get(this.imagesPath, "users", username);
            Files.createDirectories(userFolderPath);
            Path filePath = userFolderPath.resolve(fileName);

            try (InputStream inputStream = imageFile.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            uDB.setProfileImageUrl(fileName);
            this.userRepository.save(uDB);
            return fileName;

        } catch (IOException e) {
            throw new UploadFileException(this.messagesUtils.getMessage(SystemMessages.ERROR_UPLOADING_IMAGE));
        }
    }

    private void validateImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new InvalidFileException(this.messagesUtils.getMessage(SystemMessages.ERROR_FILE_EMPTY));
        }
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_GIF.getMimeType(), IMAGE_PNG.getMimeType())
                .contains(imageFile.getContentType())) {
            throw new UploadFileException(this.messagesUtils.getMessage(SystemMessages.ERROR_UPLOADING_IMAGE_FORMAT));
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    }

    public byte[] getImage(String username) {
        User uDB = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(this.messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, username)));

        Path imagePath = Paths.get(this.imagesPath, "users", uDB.getUsername(), uDB.getProfileImageUrl());
        Path defaultImagePath = Paths.get(this.imagesPath, "avatar_01.jpg");

        try {
            return Files.exists(imagePath) ? Files.readAllBytes(imagePath) : Files.readAllBytes(defaultImagePath);
        } catch (IOException e) {
            throw new InvalidFileException(this.messagesUtils.getMessage(SystemMessages.ERROR_GETTING_PROFILE_IMG));
        }
    }
}
