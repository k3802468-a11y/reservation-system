package com.reservation.vor.reservation_system.dto.reservation;

import com.reservation.vor.reservation_system.entity.ReservationSource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    @NotNull
    private Long customerId;
    private Long employeeId;
    @NotNull
    private LocalDate reservationDate;
    @NotNull
    private LocalTime reservationTime;
    @NotNull
    @Min(1)
    private Integer guestCount;
    private String recommendedBy;
    private String notes;
    private ReservationSource source;
}
