package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.auth.LoginRequest;
import com.reservation.vor.reservation_system.dto.auth.LoginResponse;
import com.reservation.vor.reservation_system.dto.auth.RefreshTokenRequest;
import com.reservation.vor.reservation_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {



    private final AuthService authService;



    // 登入
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody LoginRequest request
    ){


        return ResponseEntity.ok(
                authService.login(request)
        );

    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid
            @RequestBody RefreshTokenRequest request
    ){

        return ResponseEntity.ok(
                authService.refreshToken(
                        request.getRefreshToken()
                )
        );

    }

}