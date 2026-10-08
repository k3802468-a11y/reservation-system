package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 依電話查詢客戶
    Optional<Customer> findByPhone(String phone);

    // 檢查電話是否已存在
    boolean existsByPhone(String phone);

}