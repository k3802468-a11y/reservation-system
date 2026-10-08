package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.auth.LoginRequest;
import com.reservation.vor.reservation_system.dto.auth.LoginResponse;
import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.reservation.vor.reservation_system.security.JwtService;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final EmployeeRepository employeeRepository;


    private final PasswordEncoder passwordEncoder;


    private final JwtService jwtService;




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


        String username =
                jwtService.extractUsername(
                        refreshToken
                );


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
}