package com.ulk.readingflow.domain.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum SystemMessages {

    // Error Messages
    INTERNAL_SERVER_ERROR("error.internal_server_error"),
    AUTHENTICATION_FAIL("error.authentication_fail"),
    RESOURCE_NOT_FOUND("error.resource_not_found"),
    RESOURCE_ALREADY_EXISTS("error.resource_already_exists"),
    UNAUTHORIZED("error.unauthorized"),
    UNEXPECTED_VALUE("unexpected.value"),
    BAD_CREDENTIALS("error.bad.credentials"),
    ERROR_UPLOADING_IMAGE("error.uploading.image"),
    ERROR_UPLOADING_IMAGE_FORMAT("error.uploading.image.format"),
    ERROR_FILE_EMPTY("error.file.empty"),
    ERROR_GETTING_PROFILE_IMG("error.geting.profile.img"),
    ERROR_INVALID_HEADER("error.invalid.header"),
    ERROR_INVALID_TOKEN("error.invalid.token"),
    ERROR_GDRIVE_AUTHENTICATION("error.gdrive.authentication"),
    ERROR_UPLOADING_FILE("error.uploading.file"),
    ERROR_PARSE_JSON("error.parse.json"),
    ERROR_MAILSERVICE("error.mailservice"),

    // Authentication Messages
    AUTHENTICATION_SUCCESS("authentication.success"),
    AUTHENTICATION_AUTHENTICATED("authentication.authenticated"),
    AUTHENTICATION_LOGOUT("authentication.logout"),
    ERROR_AUTHENTICATION_FAILED("authentication.failed"),
    UNAUTHENTICATED_USER("unauthenticated.user"),

    // Instantiation Messages
    INSTANTIATION_NOT_ALLOWED("instantiation.not.allowed"),
    INSTANTIATION_NOT_ALLOWED_MESSAGE("instantiation.not.allowed.message"),

    // Request Messages
    REQUEST_OK("request.ok"),
    BAD_REQUEST("bad.request"),

    // Token Messages
    JWT_EXPIRED("token.jwt.expired"),

    // User Messages
    USER_UPLOAD_SUCCESS("user.upload.success");

    private final String keyMessage;

}
