package com.ulk.readingflow.api.v1.controllers;

import com.ulk.readingflow.api.v1.payloads.requests.AuthRequest;
import com.ulk.readingflow.api.v1.payloads.requests.dtos.UserRequestDTO;
import com.ulk.readingflow.api.v1.payloads.responses.AuthResponse;
import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserDTO;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.services.AuthenticationService;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.ulk.readingflow.domain.constants.SystemConstants.ALLOW_PATHS;
import static com.ulk.readingflow.domain.constants.SystemConstants.EXPIRATION_TIME;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = ALLOW_PATHS, maxAge = EXPIRATION_TIME)
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final MessageUtils messageUtils;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, MessageUtils messageUtils) {
        this.authenticationService = authenticationService;
        this.messageUtils = messageUtils;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> register(@RequestBody UserRequestDTO dto) {
        User user = this.authenticationService.register(dto);
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SystemResponse<AuthResponse>> authenticate(@RequestBody AuthRequest authRequest) {
        SystemResponse<AuthResponse> response = SystemResponse.<AuthResponse>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.AUTHENTICATION_SUCCESS))
                .payload(this.authenticationService.authenticate(authRequest))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<SystemResponse<String>> logout(@RequestParam String accessToken) {
        this.authenticationService.logout(accessToken);
        SystemResponse<String> response = new SystemResponse<>(
                SystemMessages.AUTHENTICATION_LOGOUT.getKeyMessage(),
                this.messageUtils.getMessage(SystemMessages.AUTHENTICATION_LOGOUT));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<SystemResponse<UserDTO>> me() {
        SystemResponse<UserDTO> response = SystemResponse.<UserDTO>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messageUtils.getMessage(SystemMessages.AUTHENTICATION_AUTHENTICATED))
                .payload(this.authenticationService.me())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
