package com.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * 生成JWT令牌
     * @param email 用户邮箱，用于标识令牌所有者
     * @param role 用户角色，用于赋予令牌相应权限
     * @return 生成的JWT令牌字符串
     */
    public String genrateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 1天有效
                .signWith(key)
                .compact();
    }

    /**
     * 从指定的令牌中获取Clains（声明）对象
     * 该方法主要用于解析JWT令牌，并提取出其中的声明信息
     *
     * @param token 需要解析的JWT令牌字符串
     * @return 解析后的Claims对象，包含令牌中的各种声明信息
     */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
