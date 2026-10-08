package com.reservation.vor.reservation_system.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service("permissionService")
public class PermissionService {

    public boolean canEditReservation() {
        EmployeeDetails employeeDetails = getEmployeeDetails();
        if (employeeDetails == null || employeeDetails.getEmployee() == null) {
            return false;
        }
        return Boolean.TRUE.equals(employeeDetails.getEmployee().getCanEditReservation());
    }

    public boolean canDeleteReservation() {
        EmployeeDetails employeeDetails = getEmployeeDetails();
        if (employeeDetails == null || employeeDetails.getEmployee() == null) {
            return false;
        }
        return Boolean.TRUE.equals(employeeDetails.getEmployee().getCanDeleteReservation());
    }

    public boolean canExportData() {
        EmployeeDetails employeeDetails = getEmployeeDetails();
        if (employeeDetails == null || employeeDetails.getEmployee() == null) {
            return false;
        }
        return Boolean.TRUE.equals(employeeDetails.getEmployee().getCanExportData());
    }

    private EmployeeDetails getEmployeeDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof EmployeeDetails details) {
            return details;
        }
        return null;
    }
}