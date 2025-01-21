package com.ulk.readingflow.core.security.jwt;

import com.ulk.readingflow.api.exceptions.InvalidTokenException;
import com.ulk.readingflow.api.exceptions.JwtException;
import com.ulk.readingflow.core.SecretProperties;
import com.ulk.readingflow.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.ulk.readingflow.domain.constants.SystemConstants.TOKEN_EXPIRATION_TIME_DAYS;

@Service
public class JwtService {

    private SecretProperties secretProperties;

    @Autowired
    public JwtService(SecretProperties secretProperties) {
        this.secretProperties = secretProperties;
    }


    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user, TOKEN_EXPIRATION_TIME_DAYS);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(expiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            extractUsernameFromToken(token);
            if (isTokenExpired(token)) {
                throw new InvalidTokenException("Token has expired");
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretProperties.getKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
