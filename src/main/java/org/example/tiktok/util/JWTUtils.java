package org.example.tiktok.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.example.tiktok.exception.TokenException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtils {

    private static final long EXPIRE_TIME= 120*60*1000;//120分钟有效
    private static final String TOKEN_SECRET="token123";  //密钥盐

    /**
     * token生成
     * @return
     */
    public static String getToken(String userId){

        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("id",userId)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;

    }
    /**
     * 校验token
     */
    public static void verifyToken(String token){
        if (token == null || token.trim().isEmpty()) {
            throw new TokenException("未登录");
        }
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET))
                    .withIssuer("auth0")
                    .build();
        try {
            verifier.verify(token);
        } catch (SignatureVerificationException e) {
            throw new TokenException("签名错误");
        } catch (TokenExpiredException e) {
            throw new TokenException("登录超时");
        } catch (AlgorithmMismatchException e) {
            throw new TokenException("算法错误");
        } catch (JWTVerificationException e) {
            throw new TokenException("token无效");
        }
        }



    public static String getId(String token) throws JWTVerificationException {
        try {
            // 创建JWT验证器
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET))
                    .withIssuer("auth0")
                    .build();

            // 验证Token
            DecodedJWT decodedJWT = verifier.verify(token);

            // 从Token中提取用户ID
            return decodedJWT.getClaim("id").asString();
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw new JWTVerificationException("Token无效或解析失败");
        }
    }

    /**
     * Parses claims from the token
     *
     * @param token the JWT token
     * @return the claims
     */
    public static DecodedJWT parseClaims(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET))
                .withIssuer("auth0")
                .build();
        return verifier.verify(token);
    }
}