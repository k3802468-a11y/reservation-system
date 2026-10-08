package com.reservation.vor.reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reservation.vor.reservation_system.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Builder.Default
    @Column(name = "can_edit_reservation")
    private Boolean canEditReservation = false;

    @Builder.Default
    @Column(name = "can_delete_reservation")
    private Boolean canDeleteReservation = false;

    @Builder.Default
    @Column(name = "can_export_data")
    private Boolean canExportData = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 一位員工可以負責多筆預約
    @OneToMany(
            mappedBy = "employee",
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<Reservation> reservations;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
