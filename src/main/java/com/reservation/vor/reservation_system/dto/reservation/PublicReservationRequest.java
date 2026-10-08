package com.reservation.vor.reservation_system.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PublicReservationRequest {
    @NotBlank private String name;
    @NotBlank private String phone;
    @NotNull private LocalDate reservationDate;
    @NotNull private LocalTime reservationTime;
    @NotNull @Min(1) private Integer guestCount;
    private String notes;
    private String recommendedBy;
}
