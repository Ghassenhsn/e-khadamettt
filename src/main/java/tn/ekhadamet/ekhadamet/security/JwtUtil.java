package tn.ekhadamet.ekhadamet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

    // 24 hours validity
    private final long expirationMs = 24 * 60 * 60 * 1000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates JWT token.
     *
     * @param identifier Usually the phone number (or CIN) used during login
     * @param role       The role name (e.g. "CITIZEN")
     * @param userId     The citizen's ID
     */
    public String generateToken(String identifier, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);

        return Jwts.builder()
                .claims(claims)
                .subject(identifier)                    // ← phone number (or CIN)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
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

    /**
     * Returns the identifier from the token (phone or CIN depending on what you set as subject)
     */
    public String extractIdentifier(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Alias if some old code still calls extractUsername
    @Deprecated
    public String extractUsername(String token) {
        return extractIdentifier(token);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validates token against the expected identifier (phone/CIN)
     */
    public boolean validateToken(String token, String expectedIdentifier) {
        try {
            return expectedIdentifier.equals(extractIdentifier(token)) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Convenience method: check if token is valid at all (no specific user check)
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}