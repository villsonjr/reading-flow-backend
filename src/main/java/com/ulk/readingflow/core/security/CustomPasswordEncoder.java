package com.ulk.readingflow.core.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        String password = rawPassword.toString();
        return BCrypt.withDefaults()
                .hashToString(12, password.toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String password = rawPassword.toString();
        return BCrypt.verifyer()
                .verify(password.toCharArray(), encodedPassword).verified;
    }
}
