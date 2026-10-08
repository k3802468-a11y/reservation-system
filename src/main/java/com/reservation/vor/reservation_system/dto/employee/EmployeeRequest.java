package com.reservation.vor.reservation_system.dto.employee;

import com.reservation.vor.reservation_system.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {
    @NotBlank
    private String name;
    @NotNull
    private Role role;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Boolean canEditReservation;
    private Boolean canDeleteReservation;
    private Boolean canExportData;
}
