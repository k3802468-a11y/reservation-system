package com.reservation.vor.reservation_system.dto.customer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CustomerResponse {

    private Long id;

    private String name;

    private String phone;

    private String notes;

    private Boolean blacklist;

    private Integer noShowCount;

}