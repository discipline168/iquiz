package com.discipline.iquiz.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    private static final long EXPIRE_TIME = 1000 * 60 * 120;
    private static final String TOKEN_SECRET = "disciplineiquiz";

    public static String generateToken(String id,String password,int role){
        return JWT.create()
                .withClaim("id",id)
                .withClaim("role",role)
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRE_TIME))
                .sign(Algorithm.HMAC256(password));
    }

    public static Map<String, Claim> getInfoByToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token    密钥
     * @param id 用户id
     * @param password 密码
     * @return
     */
    public static boolean verify(String token, String id, String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(password);

            JWTVerifier verifier = JWT.require(algorithm).withClaim("id", id).build();

            DecodedJWT jwt = verifier.verify(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
