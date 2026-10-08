package com.reservation.vor.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    // 哪一筆預約
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;



    // 哪位員工操作
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;



    // 操作內容
    // CREATE / UPDATE / DELETE / ARRIVED / NO_SHOW
    @Column(nullable = false, length = 50)
    private String action;



    // 操作時間
    @Column(name = "timestamp")
    private LocalDateTime timestamp;



    @PrePersist
    public void prePersist(){

        if(timestamp == null){

            timestamp = LocalDateTime.now();

        }

    }

}