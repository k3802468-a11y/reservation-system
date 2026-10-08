package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.notification.NotificationRequest;
import com.reservation.vor.reservation_system.dto.notification.NotificationResponse;
import com.reservation.vor.reservation_system.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;



    // 查詢全部通知
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(){

        return ResponseEntity.ok(
                notificationService.getAllNotifications()
        );

    }




    // 查詢某筆預約通知
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<NotificationResponse>> getByReservation(
            @PathVariable Long reservationId
    ){

        return ResponseEntity.ok(
                notificationService.getByReservation(
                        reservationId
                )
        );

    }





    // 建立通知
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid
            @RequestBody NotificationRequest request
    ){

        return ResponseEntity.ok(
                notificationService.createNotification(
                        request
                )
        );

    }





    // 修改通知狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<NotificationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ){

        return ResponseEntity.ok(
                notificationService.updateStatus(
                        id,
                        status
                )
        );

    }

}