package com.reservation.vor.reservation_system.config;

import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.entity.StoreSettings;
import com.reservation.vor.reservation_system.enums.Role;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.repository.StoreSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final StoreSettingsRepository storeSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${APP_ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${APP_ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        var existingAdmin =
                employeeRepository.findByUsername(adminUsername);

        if (existingAdmin.isEmpty()) {

            employeeRepository.save(
                    Employee.builder()
                            .name("System Administrator")
                            .role(Role.OWNER)
                            .username(adminUsername)
                            .password(passwordEncoder.encode(adminPassword))
                            .canEditReservation(true)
                            .canDeleteReservation(true)
                            .canExportData(true)
                            .build()
            );
        }

        if (storeSettingsRepository.findById(1L).isEmpty()) {

            storeSettingsRepository.save(
                    StoreSettings.builder()
                            .id(1L)
                            .regularClosedDays("0")
                            .build()
            );
        }
    }
}
