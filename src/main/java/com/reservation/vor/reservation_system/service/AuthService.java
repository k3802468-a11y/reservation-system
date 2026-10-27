package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.auth.LoginRequest;
import com.reservation.vor.reservation_system.dto.auth.LoginResponse;
import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final EmployeeRepository employeeRepository;


    private final PasswordEncoder passwordEncoder;


    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);




    public LoginResponse login(LoginRequest request){


        Employee employee =
                employeeRepository
                        .findByUsername(request.getUsername())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "帳號不存在"
                                )
                        );



        if(!passwordEncoder.matches(
                request.getPassword(),
                employee.getPassword()
        )){

            throw new RuntimeException(
                    "密碼錯誤"
            );

        }



        String accessToken =
                jwtService.generateToken(employee);



        String refreshToken =
                jwtService.generateRefreshToken(employee);

        redisTemplate.opsForValue().set(
                refreshTokenKey(employee.getUsername()),
                refreshToken,
                REFRESH_TOKEN_TTL
        );



        return LoginResponse.builder()

                .accessToken(accessToken)

                .refreshToken(refreshToken)

                .employeeId(employee.getId())

                .username(employee.getUsername())

                .role(employee.getRole())

                .build();

    }
    public LoginResponse refreshToken(
            String refreshToken
    ){

        if(!jwtService.isValid(refreshToken)){

            throw new RuntimeException(
                    "Refresh Token 無效"
            );

        }

        String username = jwtService.extractUsername(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey(username));
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("Refresh Token 已失效");
        }

        Employee employee =
                employeeRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );



        String newAccessToken =
                jwtService.generateToken(employee);



        return LoginResponse.builder()

                .accessToken(newAccessToken)

                .refreshToken(refreshToken)

                .employeeId(employee.getId())

                .username(employee.getUsername())

                .role(employee.getRole())

                .build();

    }

    private String refreshTokenKey(String username) {
        return "auth:refresh:" + username;
    }
}
