/*
 * @Description: JWT工具类
 * @Author: Mo Xu
 * @Date: 2021-11-03 17:17:38
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-15 20:51:34
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/JWTUtil.java
 */
package com.easytrade.easytradeapi.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import com.easytrade.easytradeapi.config.JWTConfig;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.EmailException;
import io.jsonwebtoken.ExpiredJwtException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component
public class JWTUtil {
    private static JWTConfig jwtConfig;

    public static String authorization;

    // Use applicationContext to init jwtConfig because static class cannot implemnted by Autowired
    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        jwtConfig = applicationContext.getBean(JWTConfig.class);
        authorization = jwtConfig.getAuthorization();
    }

    public static String generateToken(String value) {
        // Use calendar to get current time and calculate expiration time
        Calendar calendar = Calendar.getInstance();
        Date nowTime = calendar.getTime();
        calendar.add(Calendar.SECOND, jwtConfig.getExpiration());
        Date expirationTime = calendar.getTime();

        // ClaimMap is actually claims, parse the token can get it
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("value", value);

        // Decode secret in config to secret key and use it to create jwt token with prefix
        String secret = jwtConfig.getSecret();
        SecretKey signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String token = Jwts.builder().setClaims(claimMap).setIssuedAt(nowTime)
                .setExpiration(expirationTime).setNotBefore(nowTime).signWith(signKey).compact();
        token = jwtConfig.getPrefix() + token;

        return token;
    }

    public static String generateTokenForVerify(String account, String email) {
        // Use calendar to get current time and calculate expiration time
        Calendar calendar = Calendar.getInstance();
        Date nowTime = calendar.getTime();
        calendar.add(Calendar.SECOND, jwtConfig.getExpiration());
        Date expirationTime = calendar.getTime();

        // ClaimMap is actually claims, parse the token can get it
        HashMap<String, Object> claimMap = new HashMap<>();
        claimMap.put("account", account);
        claimMap.put("email", email);

        // Decode secret in config to secret key and use it to create jwt token with prefix
        String secret = jwtConfig.getSecret();
        SecretKey signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        String token = Jwts.builder().setClaims(claimMap).setIssuedAt(nowTime)
                .setExpiration(expirationTime).setNotBefore(nowTime).signWith(signKey).compact();
        token = jwtConfig.getPrefix() + token;

        return token;
    }

    // Need exist claims that from parsed token to refresh the token, return a new token
    public static String refreshToken(Claims claims) {
        // Use calendar to get current time and calculate expiration time
        Calendar calendar = Calendar.getInstance();
        Date nowTime = calendar.getTime();
        calendar.add(Calendar.SECOND, jwtConfig.getExpiration());
        Date expirationTime = calendar.getTime();

        // ClaimMap is actually claims, parse the token can get it
        String secret = jwtConfig.getSecret();
        SecretKey signKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        // Use exist claims to generate token
        String token = Jwts.builder().setClaims(claims).setIssuedAt(nowTime)
                .setExpiration(expirationTime).setNotBefore(nowTime).signWith(signKey).compact();
        token = jwtConfig.getPrefix() + token;

        return token;
    }

    // Parse token to claims
    public static Claims parseToken(String token) throws RuntimeException {
        String noPrefixToken = token.replace(jwtConfig.getPrefix(), "");
        String secret = jwtConfig.getSecret();

        Claims claims;

        try {
            claims = Jwts.parserBuilder().setSigningKey(secret).build()
                    .parseClaimsJws(noPrefixToken).getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }

        return claims;
    }

    // Get value from token
    public static String getValue(String token) throws RuntimeException {
        // 检查Token是否以正确前缀开头
        if (!token.startsWith("Bearer ")) {
            return null;
        }
        return parseToken(token).get("value").toString();
    }

    public static String getAccount(String token) throws RuntimeException {
        return parseToken(token).get("account").toString();
    }
    
    public static String getEmail(String token) throws RuntimeException {
        return parseToken(token).get("email").toString();
    }
}

