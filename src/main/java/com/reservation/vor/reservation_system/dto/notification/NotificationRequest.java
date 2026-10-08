package com.reservation.vor.reservation_system.dto.notification;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {


    // 對應預約 ID
    @NotNull(message = "預約ID不可為空")
    private Long reservationId;


    // 通知方式
    // LINE / SMS / PUSH
    @NotNull(message = "通知方式不可為空")
    private String channel;


    // 通知狀態
    // SENT / FAILED / PENDING
    private String status;

}
