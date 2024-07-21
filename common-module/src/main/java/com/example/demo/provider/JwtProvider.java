package com.example.demo.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * @param token
     * @return Integer
     */
    public Integer getAccountIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    public boolean validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token);
        return true;

    }
}
