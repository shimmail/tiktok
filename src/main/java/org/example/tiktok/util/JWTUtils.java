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

    private static final long EXPIRE_TIME= 1*60*1000;//1分钟有效
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
                    .withClaim("id","id")
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



    public static String getId(String token){
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
        DecodedJWT jwt = verifier.verify(token);
        String id = jwt.getClaim("userId").asString();
        return id;
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