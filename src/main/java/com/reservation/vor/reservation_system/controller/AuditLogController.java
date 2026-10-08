package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.audit.AuditLogRequest;
import com.reservation.vor.reservation_system.dto.audit.AuditLogResponse;
import com.reservation.vor.reservation_system.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {


    private final AuditLogService auditLogService;



    // 查詢全部操作紀錄
    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs(){

        return ResponseEntity.ok(
                auditLogService.getAllLogs()
        );

    }




    // 查詢某筆預約修改紀錄
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByReservation(
            @PathVariable Long reservationId
    ){

        return ResponseEntity.ok(
                auditLogService.getLogsByReservation(
                        reservationId
                )
        );

    }





    // 查詢某員工操作紀錄
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByEmployee(
            @PathVariable Long employeeId
    ){

        return ResponseEntity.ok(
                auditLogService.getLogsByEmployee(
                        employeeId
                )
        );

    }





    // 新增操作紀錄
    @PostMapping
    public ResponseEntity<AuditLogResponse> createLog(
            @Valid
            @RequestBody AuditLogRequest request
    ){

        return ResponseEntity.ok(
                auditLogService.createLog(request)
        );

    }

}
