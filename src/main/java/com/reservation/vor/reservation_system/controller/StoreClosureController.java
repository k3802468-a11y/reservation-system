package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.closure.AddClosedDateRequest;
import com.reservation.vor.reservation_system.dto.closure.ClosureSettingsResponse;
import com.reservation.vor.reservation_system.dto.closure.SetRegularClosedDaysRequest;
import com.reservation.vor.reservation_system.service.StoreClosureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/closure")
@RequiredArgsConstructor
public class StoreClosureController {

    private final StoreClosureService storeClosureService;

    @GetMapping("/settings")
    public ResponseEntity<ClosureSettingsResponse> getSettings() {
        return ResponseEntity.ok(storeClosureService.getClosureSettings());
    }

    @PutMapping("/regular-days")
    @PreAuthorize("@permissionService.canEditReservation()")
    public ResponseEntity<Void> setRegularClosedDays(@RequestBody SetRegularClosedDaysRequest request) {
        storeClosureService.setRegularClosedDays(request.getDays());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dates")
    @PreAuthorize("@permissionService.canEditReservation()")
    public ResponseEntity<Void> addClosedDate(@Valid @RequestBody AddClosedDateRequest request) {
        storeClosureService.addClosedDate(request.getDate(), request.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/dates/{id}")
    @PreAuthorize("@permissionService.canDeleteReservation()")
    public ResponseEntity<Void> removeClosedDate(@PathVariable Long id) {
        storeClosureService.removeClosedDateById(id);
        return ResponseEntity.noContent().build();
    }
}
