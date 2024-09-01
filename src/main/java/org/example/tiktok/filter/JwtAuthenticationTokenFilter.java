
package org.example.tiktok.filter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.example.tiktok.pojo.entity.LoginUser;
import org.example.tiktok.result.Result;
import org.example.tiktok.util.JWTUtils;
import org.example.tiktok.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    // 定义不需要进行Token校验的接口路径
    private static final List<String> EXCLUDED_PATHS =
            Arrays.asList("/user/login", "/user/register","/video/popular","/video/search");

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 设置响应字符编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 获取请求的URI并判断是否需要跳过Token校验
        String requestURI = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path -> requestURI.endsWith(path))) {
            // 如果是排除的路径，则直接放行
            filterChain.doFilter(request, response);
            return;
        }

        //获取token
        String token = request.getHeader("Access-Token");
        try {
            JWTUtils.verifyToken(token);
            // 解析token
            String userid = JWTUtils.getId(token);
            log.info("用户ID: {}", userid);

            // 从redis中获取用户信息
            String redisKey = "login:" + userid;
            LoginUser loginUser = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(loginUser)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(Result.error("用户未登录").toJson());
                return;
            }

            // 存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            log.error("Token处理异常: {}", e.getMessage(), e);
            // 返回token异常的错误信息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(Result.error(e.getMessage()).toJson());
            return;
        }

        // 放行
        filterChain.doFilter(request, response);
    }
}
