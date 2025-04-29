package com.example.backend.util;

import com.example.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtility {
    private static String SECRET;
    private static Long EXPIRATION;

    @Value("${jwtData.secret}")
    public void setSECRET(String value) {
        JwtUtility.SECRET = value;
    }

    @Value("${jwtData.expiration}")
    public void setEXPIRATION(Long value) {
        JwtUtility.EXPIRATION = value;
    }

    public static String generateToken(Long userIdx, String userEmail, Boolean isAdmin) {
        // Claims 객체 생성 (Map 방식)
        Claims claims = Jwts.claims();
        claims.put("userIdx", userIdx);
        claims.put("userEmail", userEmail);
        claims.put("userRole", isAdmin ? "ADMIN" : "USER");

        // jjwt 0.12.5에서는 SecretKey 객체를 Keys.hmacShaKeyFor(...)로 생성하여 사용합니다.
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                // signWith(SecretKey key, SignatureAlgorithm alg) 사용
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            // jjwt 0.12.5에서는 parser()에 setSigningKey()를 사용합니다.
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static User buildUserDataFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            User user = User.builder()
                    .userIdx(claims.get("userIdx", Long.class))
                    .userEmail(claims.get("userEmail", String.class))
                    .build();
            user.setIsAdmin("ADMIN".equals(claims.get("userRole", String.class)));
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
