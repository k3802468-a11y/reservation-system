package com.reservation.vor.reservation_system.security;

import com.reservation.vor.reservation_system.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class EmployeeDetails implements UserDetails {


    private final Employee employee;


    public EmployeeDetails(Employee employee){

        this.employee = employee;

    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){

        return List.of(
                () ->
                        "ROLE_" + employee.getRole()
        );

    }



    @Override
    public String getPassword(){

        return employee.getPassword();

    }



    @Override
    public String getUsername(){

        return employee.getUsername();

    }



    public Employee getEmployee(){

        return employee;

    }



    @Override
    public boolean isAccountNonExpired(){

        return true;

    }



    @Override
    public boolean isAccountNonLocked(){

        return true;

    }



    @Override
    public boolean isCredentialsNonExpired(){

        return true;

    }



    @Override
    public boolean isEnabled(){

        return true;

    }

}