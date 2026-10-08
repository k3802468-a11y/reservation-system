package com.reservation.vor.reservation_system.dto.reservation;

import com.reservation.vor.reservation_system.entity.ReservationSource;
import com.reservation.vor.reservation_system.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private String publicToken;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long employeeId;
    private String employeeName;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer guestCount;
    private ReservationStatus status;
    private String recommendedBy;
    private String notes;
    private ReservationSource source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
