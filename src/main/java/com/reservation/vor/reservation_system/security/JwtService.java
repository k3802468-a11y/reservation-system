package com.reservation.vor.reservation_system.security;

import com.reservation.vor.reservation_system.entity.Employee;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(
            @Value("${jwt.secret}") String secret
    ) {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Access Token
    // 給 App 呼叫 API 使用
    public String generateToken(Employee employee) {

        return Jwts.builder()
                .subject(employee.getUsername())
                .claim("employeeId", employee.getId())
                .claim("role", employee.getRole())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60
                        )
                )
                .signWith(key)
                .compact();
    }

    // Refresh Token
    // 用來換新的 Access Token
    public String generateRefreshToken(Employee employee) {

        return Jwts.builder()
                .subject(employee.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60 * 24 * 30
                        )
                )
                .signWith(key)
                .compact();
    }

    // 取得帳號
    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 驗證 Token
    public boolean isValid(String token) {

        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}