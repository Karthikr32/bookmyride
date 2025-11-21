package com.BusReservation.security;
import com.BusReservation.constants.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;
import java.time.Duration;

@Slf4j
@Component
public class JwtService {

    private final String SECRET_KEY;
    private final Long TOKEN_EXPIRATION = Duration.ofHours(6).toMillis();

    // final expects the value but when using @Value it works after the spring injection. In order to keep that final needs manual injection.
    public JwtService(@Value("${jwt.secret.key}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    public String generateToken(String subject, Role role, Boolean isVerified) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("role", role);
        claims.put("isVerified", isVerified);
        claims.put("created", new Date());

        if(role == Role.ADMIN) {
            claims.put("username", subject);
        }
        else claims.put("mobile", subject);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public String extractData(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(getKey())
                            .build()
                            .parseClaimsJws(token)   // signature checked here
                            .getBody();

        return claims.getSubject();       // this subject has mobile(user)/username(admin)
    }

    public boolean isTokenValid(String token, UserPrincipal userPrincipal) {
        try {
            String subject = extractData(token);

            Claims claims = Jwts.parser()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            final Date expireDate = claims.getExpiration();
            return userPrincipal.getUsername().equals(subject) && expireDate.after(new Date(System.currentTimeMillis()));
        }
        catch (MalformedJwtException | ExpiredJwtException | IllegalArgumentException |
               io.jsonwebtoken.security.SignatureException e) {
            return false;
        }
    }
}
