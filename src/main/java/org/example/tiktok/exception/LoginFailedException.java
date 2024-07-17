package org.example.tiktok.exception;

import org.example.tiktok.exception.BaseException;

/**
 * 登录失败
 */
public class LoginFailedException extends BaseException {
    public LoginFailedException(String msg){
        super(msg);
    }
}
