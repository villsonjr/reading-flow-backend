package com.ulk.readingflow.api.handler;

import com.ulk.readingflow.api.exceptions.AuthenticationFailException;
import com.ulk.readingflow.api.exceptions.GDriveAuthenticationException;
import com.ulk.readingflow.api.exceptions.GDriveException;
import com.ulk.readingflow.api.exceptions.InstantiationNotAllowedException;
import com.ulk.readingflow.api.exceptions.InvalidFileException;
import com.ulk.readingflow.api.exceptions.InvalidHeaderException;
import com.ulk.readingflow.api.exceptions.InvalidTokenException;
import com.ulk.readingflow.api.exceptions.JsonException;
import com.ulk.readingflow.api.exceptions.JwtException;
import com.ulk.readingflow.api.exceptions.ResourceAlreadyExistsException;
import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.api.exceptions.UnauthorizedException;
import com.ulk.readingflow.api.exceptions.UnexpectedValueException;
import com.ulk.readingflow.api.exceptions.UploadFileException;
import com.ulk.readingflow.api.v1.payloads.responses.SystemErrorResponse;
import com.ulk.readingflow.api.v1.payloads.responses.SystemResponse;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.utils.MessageUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestControllerAdvice
public class GlobalSystemExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageUtils messagesUtils;

    @Autowired
    public GlobalSystemExceptionHandler(MessageUtils messagesUtils) {
        this.messagesUtils = messagesUtils;
    }

    @ExceptionHandler({
            Exception.class,
            InstantiationNotAllowedException.class
    })
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleGlobalException(Exception ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, SystemMessages.INTERNAL_SERVER_ERROR, locale);
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class,
            JwtException.class,
            ExpiredJwtException.class,
            GDriveException.class, GDriveAuthenticationException.class
    })
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleAuthenticationExceptions(Exception ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, SystemMessages.AUTHENTICATION_FAIL, locale);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleNotFoundExceptions(Exception ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, SystemMessages.RESOURCE_NOT_FOUND, locale);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, SystemMessages.RESOURCE_ALREADY_EXISTS, locale);
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            InvalidTokenException.class,
            AuthenticationFailException.class,
    })
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleUnauthorizedExceptions(Exception ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, SystemMessages.UNAUTHORIZED, locale);
    }

    @ExceptionHandler({
            InvalidFileException.class,
            JsonException.class,
            InvalidHeaderException.class,
            UploadFileException.class,
            UnexpectedValueException.class,
    })
    public ResponseEntity<SystemResponse<SystemErrorResponse>> handleBadRequestExceptions(Exception ex, WebRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, SystemMessages.BAD_REQUEST, locale);
    }

    private ResponseEntity<SystemResponse<SystemErrorResponse>> buildErrorResponse(Exception ex, HttpStatus statusCode, SystemMessages messageKey, Locale locale) {
        SystemErrorResponse error = SystemErrorResponse.builder()
                .httpStatus(statusCode)
                .errorKey(messageKey.name())
                .details(List.of(ex.getMessage()))
                .build();

        SystemResponse<SystemErrorResponse> response = SystemResponse.<SystemErrorResponse>builder()
                .timeStamp(LocalDateTime.now())
                .message(this.messagesUtils.getMessage(messageKey, locale))
                .payload(error)
                .build();

        return ResponseEntity.status(statusCode).body(response);
    }
}
