package com.example.backend.util;

import com.example.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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
            Claims claims;
            if (isAdmin) {
                claims = Jwts.claims().add("userIdx", userIdx).add("userEmail", userEmail).add("userRole", "ADMIN").build();
            } else {
                claims = Jwts.claims().add("userIdx", userIdx).add("userEmail", userEmail).add("userRole", "USER").build();
            }
            return Jwts.builder().setClaims(claims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                    .signWith(SignatureAlgorithm.HS256, SECRET)
                    .compact();
        }

        public static boolean validateToken(String token) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
                Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getBody();
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static User buildUserDataFromToken(String token) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
                Claims claim = Jwts.parser().verifyWith(key).build().parseClaimsJws(token).getBody();
                User user = User.builder().userIdx(claim.get("userIdx", Long.class)).userEmail(claim.get("userEmail", String.class)).build();
                user.setIsAdmin(claim.get("userRole", String.class).equals("ADMIN"));
                return user;
            } catch (Exception e) {
                return null;
            }
        }
}
