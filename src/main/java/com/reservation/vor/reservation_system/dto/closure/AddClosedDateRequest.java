package com.reservation.vor.reservation_system.dto.closure;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddClosedDateRequest {
    @NotNull(message = "休假日期不能為空")
    private LocalDate date;

    private String reason;
}
