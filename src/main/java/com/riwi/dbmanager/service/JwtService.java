package com.riwi.dbmanager.service;

import com.riwi.dbmanager.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String generateToken(User user) {
        return buildToken(user, expiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpiration);
    }

    //Builds a JWT token
    private String buildToken(User user, Long expiration) {
        return Jwts.builder()
                .setId(user.getId().toString())
                .addClaims(Map.of("name", user.getName()))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                //.signWith(SignatureAlgorithm.HS256, secret)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Extracts the email (subject) from the token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    //Validates the token's signature/expiration and matches it to the given user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            Claims claims = getClaims(token);
            return email.equals(userDetails.getUsername()) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}