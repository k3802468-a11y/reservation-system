package com.reservation.vor.reservation_system.dto.audit;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequest {


    @NotNull(message = "預約ID不可為空")
    private Long reservationId;


    @NotNull(message = "員工ID不可為空")
    private Long employeeId;


    @NotNull(message = "操作內容不可為空")
    private String action;

}
