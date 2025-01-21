package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserDTO;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.UserService;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/v1/users")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class UserController {

    private final UserService userService;
    private final MessageUtils messageUtils;

    @Autowired
    public UserController(UserService userService, MessageUtils messageUtils) {
        this.userService = userService;
        this.messageUtils = messageUtils;
    }

    @GetMapping()
    public ResponseEntity< SystemResponse<List<UserDTO>>> listAllUsers() {
        SystemResponse<List<UserDTO>> response = SystemResponse.<List<UserDTO>>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.userService.listAllUsers())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/")
    public ResponseEntity<SystemResponse<UserDTO>> updateUser(@RequestBody UserDTO dto) {
        SystemResponse<UserDTO> response = SystemResponse.<UserDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.REQUEST_OK))
                .payload(this.userService.update(dto))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/upload-profile-image")
    public ResponseEntity<SystemResponse<String>> uploadProfileImage(@RequestParam("username") String username,
                                                                      @RequestParam("image") MultipartFile imageFile) {
        SystemResponse<String> response = SystemResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.USER_UPLOAD_SUCCESS))
                .payload(this.userService.uploadProfileImage(username, imageFile))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{username}/profile-image",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getImage(username));
    }
}
