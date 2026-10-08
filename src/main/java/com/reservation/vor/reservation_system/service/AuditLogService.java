package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.audit.AuditLogRequest;
import com.reservation.vor.reservation_system.dto.audit.AuditLogResponse;
import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.entity.Reservation;
import com.reservation.vor.reservation_system.entity.AuditLog;
import com.reservation.vor.reservation_system.repository.AuditLogRepository;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuditLogService {


    private final AuditLogRepository auditLogRepository;

    private final ReservationRepository reservationRepository;

    private final EmployeeRepository employeeRepository;



    // 查詢全部紀錄
    public List<AuditLogResponse> getAllLogs(){

        return auditLogRepository
                .findAllByOrderByTimestampDesc()
                .stream()
                .map(this::toResponse)
                .toList();

    }




    // 查詢某筆預約紀錄
    public List<AuditLogResponse> getLogsByReservation(
            Long reservationId
    ){

        return auditLogRepository
                .findByReservationId(reservationId)
                .stream()
                .map(this::toResponse)
                .toList();

    }





    // 查詢某員工操作紀錄
    public List<AuditLogResponse> getLogsByEmployee(
            Long employeeId
    ){

        return auditLogRepository
                .findByEmployeeId(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();

    }





    // 新增操作紀錄
    public AuditLogResponse createLog(
            AuditLogRequest request
    ){


        Reservation reservation =
                reservationRepository.findById(
                                request.getReservationId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found"
                                )
                        );



        Employee employee =
                employeeRepository.findById(
                                request.getEmployeeId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );



        AuditLog log =
                AuditLog.builder()
                        .reservation(reservation)
                        .employee(employee)
                        .action(request.getAction())
                        .build();



        auditLogRepository.save(log);



        return toResponse(log);

    }





    // Entity 轉 Response
    private AuditLogResponse toResponse(
            AuditLog log
    ){

        return AuditLogResponse.builder()

                .id(log.getId())

                .reservationId(
                        log.getReservation().getId()
                )

                .employeeId(
                        log.getEmployee().getId()
                )

                .employeeName(
                        log.getEmployee().getName()
                )

                .action(
                        log.getAction()
                )

                .timestamp(
                        log.getTimestamp()
                )

                .build();

    }

}