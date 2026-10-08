package com.reservation.vor.reservation_system.dto.auth;

import lombok.*;
import com.reservation.vor.reservation_system.enums.Role;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {


    private String accessToken;


    private String refreshToken;


    private Long employeeId;


    private String username;


    private Role role;

}
