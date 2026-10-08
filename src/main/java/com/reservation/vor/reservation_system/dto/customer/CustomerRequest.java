package com.reservation.vor.reservation_system.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "姓名不可空白")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "電話不可空白")
    @Size(max = 20)
    private String phone;

    private String notes;

}