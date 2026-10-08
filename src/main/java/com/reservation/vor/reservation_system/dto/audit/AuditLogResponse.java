package com.reservation.vor.reservation_system.dto.audit;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {


    private Long id;


    private Long reservationId;


    private Long employeeId;


    private String employeeName;


    private String action;


    private LocalDateTime timestamp;

}
