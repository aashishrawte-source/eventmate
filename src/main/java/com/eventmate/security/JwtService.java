package com.eventmate.security;

import com.eventmate.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET = "SuperSecretKeyThatIsAtLeast32CharsLongForJWTs123";
    private static final long EXPIRATION_MS = 1000 * 60 * 60;
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(User user) {
        return Jwts.builder()
            .subject(user.getEmail())
            .claim("role", user.getRole().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(key)
            .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
