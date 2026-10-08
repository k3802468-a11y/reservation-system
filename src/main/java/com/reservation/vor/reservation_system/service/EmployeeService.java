package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.employee.EmployeeRequest;
import com.reservation.vor.reservation_system.dto.employee.EmployeeResponse;
import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {


    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    // 查詢全部員工
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // 依 ID 查詢員工
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return toResponse(employee);
    }


    // 新增員工
    public EmployeeResponse createEmployee(EmployeeRequest request) {


        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }


        Employee employee = Employee.builder()
                .name(request.getName())
                .role(request.getRole())
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .canEditReservation(
                        request.getCanEditReservation()
                )
                .canDeleteReservation(
                        request.getCanDeleteReservation()
                )
                .canExportData(
                        request.getCanExportData()
                )
                .build();


        employeeRepository.save(employee);

        return toResponse(employee);
    }



    // 修改員工
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {


        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));


        employee.setName(request.getName());

        employee.setRole(request.getRole());

        employee.setUsername(request.getUsername());

        employee.setCanEditReservation(
                request.getCanEditReservation()
        );

        employee.setCanDeleteReservation(
                request.getCanDeleteReservation()
        );

        employee.setCanExportData(
                request.getCanExportData()
        );


        employeeRepository.save(employee);


        return toResponse(employee);
    }



    // 刪除員工
    public void deleteEmployee(Long id) {

        employeeRepository.deleteById(id);

    }



    // Entity 轉 Response
    private EmployeeResponse toResponse(Employee employee) {

        return EmployeeResponse.builder()

                .id(employee.getId())

                .name(employee.getName())

                .role(employee.getRole())

                .username(employee.getUsername())

                .canEditReservation(
                        employee.getCanEditReservation()
                )

                .canDeleteReservation(
                        employee.getCanDeleteReservation()
                )

                .canExportData(
                        employee.getCanExportData()
                )

                .createdAt(employee.getCreatedAt())

                .build();
    }

}
