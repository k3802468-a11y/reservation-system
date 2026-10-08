package com.reservation.vor.reservation_system.dto.closure;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ClosureSettingsResponse {
    private List<Integer> regularClosedDays; // 0=Sun, 1=Mon, ..., 6=Sat
    private List<ClosedDateItem> closedDates;

    @Getter
    @Setter
    @Builder
    public static class ClosedDateItem {
        private Long id;
        private LocalDate date;
        private String reason;
    }
}
