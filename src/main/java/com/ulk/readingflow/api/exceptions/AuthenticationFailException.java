package com.ulk.readingflow.api.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailException extends AuthenticationException {
    public AuthenticationFailException(String msg) {
        super(msg);
    }
}
