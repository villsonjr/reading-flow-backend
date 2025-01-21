package com.ulk.readingflow.infraestructure.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.ulk.readingflow.api.exceptions.AuthenticationFailException;
import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.v1.payloads.requests.AuthRequest;
import com.ulk.readingflow.api.v1.payloads.requests.dtos.UserRequestDTO;
import com.ulk.readingflow.api.v1.payloads.responses.AuthResponse;
import com.ulk.readingflow.api.v1.payloads.responses.dtos.UserDTO;
import com.ulk.readingflow.core.security.jwt.JwtService;
import com.ulk.readingflow.domain.entities.Role;
import com.ulk.readingflow.domain.entities.Token;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.RoleEnum;
import com.ulk.readingflow.domain.enumerations.StatusEnum;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final MessageUtils messagesUtils;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(
            UserService userService, RoleService roleService,
            TokenService tokenService, AuthenticationManager authenticationManager,
            MessageUtils messagesUtils, JwtService jwtService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.messagesUtils = messagesUtils;
        this.jwtService = jwtService;
    }

    public User register(UserRequestDTO userDTO) {
        if (userService.isRegistered(userDTO.getUsername())) {
            throw new ResourceNotFoundException(
                    messagesUtils.getMessage(SystemMessages.RESOURCE_ALREADY_EXISTS, userDTO.getUsername())
            );
        }

        User user = buildUserFromDTO(userDTO);
        user.setPassword(hashPassword(userDTO.getPassword()));

        Role userRole = roleService.findByDescription(RoleEnum.USER);
        user.setRoles(Collections.singleton(userRole));

        return userService.create(user);
    }

    private User buildUserFromDTO(UserRequestDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .gender(userDTO.getGender())
                .profileImageUrl("avatar_01.jpg")
                .kindleMail(userDTO.getKindleMail())
                .status(StatusEnum.ACTIVE)
                .build();
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication auth = authenticateUser(authRequest);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userService.loadUserByUsername(authRequest.getUsername());
        Token token = validateToken(user);

        return AuthResponse.builder()
                .accessToken(token.getAccessToken())
                .build();
    }

    private Authentication authenticateUser(AuthRequest authRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );
    }

    private Token validateToken(User user) {
        List<Token> tokenList = tokenService.getValidTokens(user.getId());
        if (!tokenList.isEmpty()) {
            revokeTokens(tokenList);
        }

        String accessToken = jwtService.generateToken(user);
        LocalDateTime expirationDate = extractExpirationDate(accessToken);

        Token token = Token.builder()
                .user(user)
                .accessToken(accessToken)
                .expirationDate(expirationDate)
                .expired(false)
                .revoked(false)
                .build();

        return tokenService.save(token);
    }

    private void revokeTokens(List<Token> tokens) {
        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenService.saveTokens(tokens);
    }

    private LocalDateTime extractExpirationDate(String token) {
        return LocalDateTime.ofInstant(
                jwtService.extractExpiration(token).toInstant(),
                ZoneId.systemDefault()
        );
    }

    public void logout(String accessToken) {
        if (jwtService.isValidToken(accessToken)) {
            String username = jwtService.extractUsernameFromToken(accessToken);
            User user = userService.loadUserByUsername(username);
            List<Token> tokenList = tokenService.getValidTokens(user.getId());
            if (!tokenList.isEmpty()) {
                revokeTokens(tokenList);
            }
        }
    }

    public UserDTO me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(authentication);

        User user = extractUserFromAuthentication(authentication);
        return UserDTO.fromEntity(user);
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationFailException(
                    messagesUtils.getMessage(SystemMessages.UNAUTHENTICATED_USER)
            );
        }
    }

    private User extractUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new AuthenticationFailException(
                    messagesUtils.getMessage(SystemMessages.UNAUTHENTICATED_USER)
            );
        }
        return user;
    }
}
