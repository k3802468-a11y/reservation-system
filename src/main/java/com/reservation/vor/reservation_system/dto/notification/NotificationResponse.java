package com.reservation.vor.reservation_system.dto.notification;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {


    private Long id;


    // 預約 ID
    private Long reservationId;


    // 通知方式
    private String channel;


    // 發送狀態
    private String status;


    // 發送時間
    private LocalDateTime sentAt;

}
