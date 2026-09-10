package com.projects.task_master.utils;

import com.projects.task_master.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String secretKey;
    private static final long EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;

    public String generateToken(User user){
        return Jwts.builder().
                id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Claims validateToken(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractJti(String token) {
        return validateToken(token).getId();
    }

    public Date extractExpiration(String token) {
        return validateToken(token).getExpiration();
    }

}
