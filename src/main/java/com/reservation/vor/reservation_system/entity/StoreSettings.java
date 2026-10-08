package com.reservation.vor.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSettings {

    @Id
    private Long id;

    // Regular closed days stored as comma-separated days of week (0=Sunday, 1=Monday, ..., 6=Saturday)
    // e.g. "0" means Sunday closed, "0,6" means Sunday and Saturday closed
    @Column(name = "regular_closed_days", length = 50)
    private String regularClosedDays;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
