package com.reservation.vor.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    // 對應哪筆預約
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;



    // 通知方式
    // LINE / SMS / PUSH
    @Column(nullable = false, length = 20)
    private String channel;



    // 發送狀態
    // SENT / FAILED / PENDING
    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "SENT";



    // 發送時間
    @Column(name = "sent_at")
    private LocalDateTime sentAt;



    @PrePersist
    public void prePersist(){

        if(sentAt == null){

            sentAt = LocalDateTime.now();

        }

    }

}
