package com.reservation.vor.reservation_system.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {


    @NotBlank(message = "帳號不可為空")
    private String username;


    @NotBlank(message = "密碼不可為空")
    private String password;

}
