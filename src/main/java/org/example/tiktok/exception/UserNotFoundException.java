package org.example.tiktok.exception;


/**
 * 账号不存在异常
 */
public class UserNotFoundException extends BaseException {

    public UserNotFoundException() {
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
