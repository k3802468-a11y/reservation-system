package com.reservation.vor.reservation_system.dto.closure;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SetRegularClosedDaysRequest {
    // List of day of week (0=Sunday, 1=Monday, ..., 6=Saturday)
    private List<Integer> days;
}
