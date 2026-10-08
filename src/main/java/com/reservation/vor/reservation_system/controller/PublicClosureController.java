package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.closure.ClosureSettingsResponse;
import com.reservation.vor.reservation_system.service.StoreClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/closure")
@RequiredArgsConstructor
public class PublicClosureController {

    private final StoreClosureService storeClosureService;

    @GetMapping("/settings")
    public ResponseEntity<ClosureSettingsResponse> getPublicClosureSettings() {
        return ResponseEntity.ok(storeClosureService.getClosureSettings());
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkClosed(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isClosed = storeClosureService.isDateClosed(date);
        return ResponseEntity.ok(Collections.singletonMap("closed", isClosed));
    }

    @GetMapping("/closed-dates")
    public ResponseEntity<List<LocalDate>> getClosedDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(storeClosureService.getClosedDatesInRange(start, end));
    }
}
