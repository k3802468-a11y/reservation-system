package com.reservation.vor.reservation_system.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.reservation.vor.reservation_system.entity.Reservation;

import java.time.LocalDate;


public interface StatisticsRepository extends JpaRepository<Reservation, Long> {


    // 今日預約數
    @Query(value = """
            SELECT COUNT(*)
            FROM reservation
            WHERE reservation_date = :date
            """,
            nativeQuery = true)
    Long countTodayReservations(
            @Param("date") LocalDate date
    );



    // 完成數
    @Query(value = """
            SELECT COUNT(*)
            FROM reservation
            WHERE reservation_date = :date
            AND status = 'COMPLETED'
            """,
            nativeQuery = true)
    Long countCompleted(
            @Param("date") LocalDate date
    );



    // No Show數
    @Query(value = """
            SELECT COUNT(*)
            FROM reservation
            WHERE reservation_date = :date
            AND status = 'NO_SHOW'
            """,
            nativeQuery = true)
    Long countNoShow(
            @Param("date") LocalDate date
    );



    // 取消數
    @Query(value = """
            SELECT COUNT(*)
            FROM reservation
            WHERE reservation_date = :date
            AND status = 'CANCELLED'
            """,
            nativeQuery = true)
    Long countCancelled(
            @Param("date") LocalDate date
    );



    // 新客數
    // 只有一筆預約紀錄的客人
    @Query(value = """
            SELECT COUNT(*)
            FROM customer c
            WHERE
            (
                SELECT COUNT(*)
                FROM reservation r
                WHERE r.customer_id = c.id
            ) = 1
            """,
            nativeQuery = true)
    Long countNewCustomers();



    // 老客數
    // 預約超過一次
    @Query(value = """
            SELECT COUNT(*)
            FROM customer c
            WHERE
            (
                SELECT COUNT(*)
                FROM reservation r
                WHERE r.customer_id = c.id
            ) > 1
            """,
            nativeQuery = true)
    Long countReturningCustomers();

}
