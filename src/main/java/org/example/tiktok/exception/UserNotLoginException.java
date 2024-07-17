package org.example.tiktok.exception;

import org.example.tiktok.exception.BaseException;

/**
 * 用户未登录异常
 */
public class UserNotLoginException extends BaseException {

    public UserNotLoginException() {
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

}
