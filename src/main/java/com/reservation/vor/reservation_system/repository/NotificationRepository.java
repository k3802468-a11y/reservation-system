package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository
        extends JpaRepository<Notification, Long> {


    // 查詢某筆預約的通知紀錄
    List<Notification> findByReservationId(Long reservationId);



    // 查詢某種通知方式
    // 例如全部 LINE 通知
    List<Notification> findByChannel(String channel);



    // 查詢失敗通知
    List<Notification> findByStatus(String status);

}