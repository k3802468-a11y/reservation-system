package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.reservation.PublicReservationRequest;
import com.reservation.vor.reservation_system.dto.reservation.ReservationResponse;
import com.reservation.vor.reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/reservations")
@RequiredArgsConstructor
public class PublicReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody PublicReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createPublicReservation(request));
    }

    @GetMapping("/{token}")
    public ResponseEntity<ReservationResponse> get(@PathVariable String token) {
        return ResponseEntity.ok(reservationService.getPublicReservation(token));
    }

    @PatchMapping("/{token}")
    public ResponseEntity<ReservationResponse> update(@PathVariable String token, @Valid @RequestBody PublicReservationRequest request) {
        return ResponseEntity.ok(reservationService.updatePublicReservation(token, request));
    }

    @PostMapping("/{token}/cancel")
    public ResponseEntity<ReservationResponse> cancel(@PathVariable String token) {
        return ResponseEntity.ok(reservationService.cancelPublicReservation(token));
    }
}
