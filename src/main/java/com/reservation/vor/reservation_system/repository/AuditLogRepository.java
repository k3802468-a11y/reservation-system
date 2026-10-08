package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {


    // 查詢某筆預約的所有操作紀錄
    List<AuditLog> findByReservationId(Long reservationId);


    // 查詢某位員工的操作紀錄
    List<AuditLog> findByEmployeeId(Long employeeId);


    // 依時間排序查詢全部紀錄
    List<AuditLog> findAllByOrderByTimestampDesc();

}