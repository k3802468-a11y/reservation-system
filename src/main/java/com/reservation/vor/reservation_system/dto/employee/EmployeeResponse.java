package com.reservation.vor.reservation_system.dto.employee;

import lombok.*;
import com.reservation.vor.reservation_system.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {


    private Long id;


    private String name;


    private Role role;


    private String username;


    private Boolean canEditReservation;


    private Boolean canDeleteReservation;


    private Boolean canExportData;


    private LocalDateTime createdAt;

}
