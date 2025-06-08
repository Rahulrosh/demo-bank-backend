package com.rahul.demo_bank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication){
        String userName= authentication.getName();
        Date currentDate= new Date();
        Date expireDate= new Date(currentDate.getTime()+jwtExpirationDate);

        return Jwts.builder()
                .claim("sub", userName)
                .claim("iat", currentDate.getTime() / 1000)
                .claim("exp", expireDate.getTime() / 1000)
                .signWith(key())
                .compact();
    }

    private SecretKey key(){
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(key()) // ✅ New API style
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(key()) // ✅ New API style
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
