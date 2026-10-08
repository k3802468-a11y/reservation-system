package com.reservation.vor.reservation_system.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {


    @NotBlank(message = "Refresh Token 不可為空")
    private String refreshToken;

}