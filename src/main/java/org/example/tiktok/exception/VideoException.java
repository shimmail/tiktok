package org.example.tiktok.exception;

/**
 * 业务异常
 */
public class VideoException extends RuntimeException {

    public VideoException() {
    }

    public VideoException(String msg) {
        super(msg);
    }

}
