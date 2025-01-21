package com.ulk.readingflow.api.exceptions;

public class GDriveAuthenticationException extends GDriveException {
    public GDriveAuthenticationException(String message) {
        super(message);
    }

    public GDriveAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}