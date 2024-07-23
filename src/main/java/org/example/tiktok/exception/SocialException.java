package org.example.tiktok.exception;

public class SocialException extends RuntimeException {

    public SocialException(String message) {
        super(message);
    }

    public SocialException(String message, Throwable cause) {
        super(message, cause);
    }
}