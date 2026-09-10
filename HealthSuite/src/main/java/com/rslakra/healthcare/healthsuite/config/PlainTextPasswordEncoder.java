package com.rslakra.healthcare.healthsuite.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Plain-text password encoder for local development with H2 seed data.
 * Replace with BCryptPasswordEncoder in production.
 */
public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return rawPassword.toString().equals(encodedPassword);
    }
}
