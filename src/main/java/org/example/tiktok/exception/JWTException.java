package org.example.tiktok.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class JWTException extends RuntimeException {

    public JWTException(String message) {
        super(message);
    }

    public JWTException(String message, Throwable cause) {
        super(message, cause);
    }
}