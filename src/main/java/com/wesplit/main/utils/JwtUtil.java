package com.wesplit.main.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${myapp.secret}")
    private String SECRET_KEY;
    private final RedisTemplate redisTemplate;

    @Autowired
    JwtUtil(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public void logout(HttpServletRequest request) {
        String authorizationHeader=request.getHeader("Authorization");
        String token;
        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            token=authorizationHeader.substring(7);
            redisTemplate.opsForValue().set(token,"expired",3600, TimeUnit.SECONDS);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiration time
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        String value=(String) redisTemplate.opsForValue().get(token);
        if(value!=null){
            throw new ExpiredJwtException(null,null,"Session Expired",null);
        }
        return !isTokenExpired(token);
    }
}
