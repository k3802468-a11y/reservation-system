package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.customer.CustomerRequest;
import com.reservation.vor.reservation_system.dto.customer.CustomerResponse;
import com.reservation.vor.reservation_system.entity.Customer;
import com.reservation.vor.reservation_system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;


    // 查詢全部
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    // 依 ID 查詢
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return toResponse(customer);
    }


    // 依電話查詢
    public CustomerResponse getCustomerByPhone(String phone) {

        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return toResponse(customer);
    }


    // 新增
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }


        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .notes(request.getNotes())
                .build();


        customerRepository.save(customer);

        return toResponse(customer);
    }


    // 修改
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));


        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setNotes(request.getNotes());


        customerRepository.save(customer);

        return toResponse(customer);
    }


    // 刪除
    public void deleteCustomer(Long id) {

        customerRepository.deleteById(id);
    }


    // Entity轉DTO
    private CustomerResponse toResponse(Customer customer) {

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .notes(customer.getNotes())
                .blacklist(customer.getBlacklist())
                .noShowCount(customer.getNoShowCount())
                .build();
    }
}