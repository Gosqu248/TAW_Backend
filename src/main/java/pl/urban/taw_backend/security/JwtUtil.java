package pl.urban.taw_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.security.Keys;
import pl.urban.taw_backend.model.User;

@Component
public class JwtUtil {

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key secretKey;

    public void rotateKey() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateToken(@NotNull User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        return createToken(claims, user.getEmail());
    }

    public String extractSubjectFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @PostConstruct
    private void init() {
        rotateKey();
    }
}
