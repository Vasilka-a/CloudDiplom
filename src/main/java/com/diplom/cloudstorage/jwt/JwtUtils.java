package com.diplom.cloudstorage.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaimBody(token, Claims::getSubject);
    }

    private Jws<Claims> extractClaims(String bearerToken) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(bearerToken);
    }

    public <T> T extractClaimBody(String bearerToken, Function<Claims, T> claimsResolver) {
        Jws<Claims> jwsClaims = extractClaims(bearerToken);
        return claimsResolver.apply(jwsClaims.getBody());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, Decoders.BASE64.decode(secretKey))
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaimBody(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
