package org.example.tiktok.exception;

/**
 * 业务异常
 */
public class TokenException extends RuntimeException {

    public TokenException() {
    }

    public TokenException(String msg) {
        super(msg);
    }

}
