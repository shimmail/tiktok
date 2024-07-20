package org.example.tiktok.interceptor;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.example.tiktok.exception.JWTException;
import org.example.tiktok.exception.TokenException;
import org.example.tiktok.result.Result;
import org.example.tiktok.util.JWTUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Component
@Slf4j
/**
 * JWT验证拦截器
 */
public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、从请求头中获取令牌
        String token = request.getHeader("Access-Token");
        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            JWTUtils.verifyToken(token);
            String id = JWTUtils.getId(token);
            log.info("校验通过");
            return true;
        } catch (Exception ex) {
            log.error("Token验证失败: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(Result.error("Token验证失败: " + ex.getMessage()).toJson());
        }
        return false;
        }
    }