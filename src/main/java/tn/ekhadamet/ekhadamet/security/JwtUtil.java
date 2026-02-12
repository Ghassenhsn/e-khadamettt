package tn.ekhadamet.ekhadamet.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long normalExpirationMs = 24 * 60 * 60 * 1000;      // 24h
    private final long setupExpirationMs   = 30 * 60 * 1000;          // 30 min for password setup

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Normal auth token
    public String generateToken(String email, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("type", "auth");

        return Jwts.builder()
                .claims(claims)
                .subject(email)                             // ← email as subject
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + normalExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // Short-lived token for password setup (if you still use it)
    public String generatePasswordSetupToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password_setup");
        claims.put("userId", userId);

        return Jwts.builder()
                .claims(claims)
                .subject(email)                             // ← email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + setupExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token, String expectedEmail) {
        try {
            return expectedEmail.equals(extractEmail(token)) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Password setup specific
    public boolean isPasswordSetupTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "password_setup".equals(claims.get("type", String.class)) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromPasswordSetupToken(String token) {
        try {
            return extractAllClaims(token).get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getEmailFromPasswordSetupToken(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}