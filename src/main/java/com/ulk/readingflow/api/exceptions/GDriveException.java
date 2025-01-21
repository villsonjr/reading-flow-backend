package com.ulk.readingflow.api.exceptions;

public class GDriveException extends RuntimeException {
    public GDriveException(String message) {
        super(message);
    }

    public GDriveException(String message, Throwable cause) {
        super(message, cause);
    }
}