package org.example.tiktok.interceptor;


import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tiktok.exception.JWTException;
import org.example.tiktok.util.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT验证拦截器
 */
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        try {
            JWTUtils.verify(token); // 验证令牌
            return true; // 放行请求
        } catch (SignatureVerificationException e) {
            throw new JWTException("无效签名");
        } catch (TokenExpiredException e) {
            throw new JWTException("token过期");
        } catch (AlgorithmMismatchException e) {
            throw new JWTException("token算法不一致");
        } catch (Exception e) {
            throw new JWTException("token失效");
        }
    }
}
