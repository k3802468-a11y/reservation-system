package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.reservation.ReservationRequest;
import com.reservation.vor.reservation_system.dto.reservation.ReservationResponse;
import com.reservation.vor.reservation_system.entity.ReservationStatus;
import com.reservation.vor.reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(date == null ? reservationService.getAllReservations() : reservationService.getReservationsByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@permissionService.canEditReservation()")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@permissionService.canEditReservation()")
    public ResponseEntity<ReservationResponse> updateStatus(@PathVariable Long id, @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(reservationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.canDeleteReservation()")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
