package com.reservation.vor.reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 所屬客人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;


    // 負責員工（可沒有）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee employee;


    // 預約日期
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;


    // 預約時間
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;


    // 預約人數
    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;


    // 預約狀態
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status =
            ReservationStatus.PENDING;


    // 推薦店員（選填）
    @Column(name = "recommended_by")
    private String recommendedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationSource source = ReservationSource.WEBSITE;

    @Column(name = "public_token", unique = true, length = 36)
    private String publicToken;


    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
        if (publicToken == null) publicToken = UUID.randomUUID().toString();
    }



    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

}
