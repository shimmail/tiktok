package org.example.tiktok.exception;

/**
 * 业务异常
 */
public class CommentException extends RuntimeException {

    public CommentException() {
    }

    public CommentException(String msg) {
        super(msg);
    }

}
