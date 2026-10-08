package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.Reservation;
import com.reservation.vor.reservation_system.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByPublicToken(String publicToken);


    // 查某一天的所有預約
    List<Reservation> findByReservationDate(
            LocalDate reservationDate
    );


    // 查某客人的歷史預約
    List<Reservation> findByCustomerId(
            Long customerId
    );


    // 查某員工負責的預約
    List<Reservation> findByEmployeeId(
            Long employeeId
    );


    // 查某日期並依時間排序（時間軸）
    List<Reservation> findByReservationDateOrderByReservationTimeAsc(
            LocalDate reservationDate
    );


    // 查某日期某狀態（Bug 3 fix: 參數型別由 String 改為 ReservationStatus）
    List<Reservation> findByReservationDateAndStatus(
            LocalDate reservationDate,
            ReservationStatus status
    );

}
