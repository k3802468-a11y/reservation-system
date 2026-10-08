package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    // 透過帳號查詢員工
    Optional<Employee> findByUsername(String username);


    // 檢查帳號是否存在
    boolean existsByUsername(String username);

}