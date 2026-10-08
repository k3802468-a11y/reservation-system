package com.reservation.vor.reservation_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String name;


    @Column(nullable = false, unique = true, length = 20)
    private String phone;


    @Column(columnDefinition = "TEXT")
    private String notes;


    @Builder.Default
    private Boolean blacklist = false;


    @Column(name = "no_show_count")
    @Builder.Default
    private Integer noShowCount = 0;


    // 一位客人可以有多筆預約
    @OneToMany(mappedBy = "customer",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reservation> reservations;

}