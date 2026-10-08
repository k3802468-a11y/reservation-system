package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.reservation.ReservationRequest;
import com.reservation.vor.reservation_system.dto.reservation.ReservationResponse;
import com.reservation.vor.reservation_system.dto.reservation.PublicReservationRequest;
import com.reservation.vor.reservation_system.entity.Customer;
import com.reservation.vor.reservation_system.entity.Employee;
import com.reservation.vor.reservation_system.entity.Reservation;
import com.reservation.vor.reservation_system.entity.ReservationStatus;
import com.reservation.vor.reservation_system.repository.CustomerRepository;
import com.reservation.vor.reservation_system.repository.EmployeeRepository;
import com.reservation.vor.reservation_system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.reservation.vor.reservation_system.entity.ReservationSource;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final StoreClosureService storeClosureService;

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReservationResponse> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByReservationDateOrderByReservationTimeAsc(date)
                .stream().map(this::toResponse).toList();
    }

    public ReservationResponse getReservationById(Long id) {
        return toResponse(findReservation(id));
    }

    public ReservationResponse getPublicReservation(String token) {
        return toResponse(findPublicReservation(token));
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Reservation reservation = Reservation.builder()
                .customer(customer)
                .employee(findEmployee(request.getEmployeeId()))
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .guestCount(request.getGuestCount())
                .recommendedBy(request.getRecommendedBy())
                .notes(request.getNotes())
                .source(request.getSource() == null ? com.reservation.vor.reservation_system.entity.ReservationSource.WALK_IN : request.getSource())
                .status(ReservationStatus.PENDING)
                .build();
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse createPublicReservation(PublicReservationRequest request) {
        if (storeClosureService.isDateClosed(request.getReservationDate())) {
            throw new IllegalArgumentException("該日期為公休日／休假日，無法預約");
        }
        validateReservationTime(request.getReservationTime());
        Customer customer = customerRepository.findByPhone(request.getPhone()).orElseGet(() ->
                customerRepository.save(Customer.builder().name(request.getName()).phone(request.getPhone()).build()));
        if (!customer.getName().equals(request.getName())) {
            customer.setName(request.getName());
            customerRepository.save(customer); // Bug 4 fix: 明確 save，不依賴 dirty-checking
        }
        Reservation reservation = Reservation.builder()
                .customer(customer)
                .reservationDate(request.getReservationDate())
                .reservationTime(request.getReservationTime())
                .guestCount(request.getGuestCount())
                .notes(request.getNotes())
                .recommendedBy(request.getRecommendedBy())
                .source(ReservationSource.WEBSITE)
                .status(ReservationStatus.PENDING)
                .build();
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse updatePublicReservation(String token, PublicReservationRequest request) {
        if (storeClosureService.isDateClosed(request.getReservationDate())) {
            throw new IllegalArgumentException("該日期為公休日／休假日，無法預約");
        }
        validateReservationTime(request.getReservationTime());
        Reservation reservation = findPublicReservation(token);
        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("This reservation can no longer be changed");
        }

        // Bug 1 fix: 若電話改變，查找或建立新的 Customer，避免覆蓋原客戶資料或引發 unique constraint
        String newPhone = request.getPhone();
        Customer currentCustomer = reservation.getCustomer();
        Customer customer;
        if (currentCustomer.getPhone().equals(newPhone)) {
            // 電話相同，只更新姓名
            currentCustomer.setName(request.getName());
            customer = customerRepository.save(currentCustomer);
        } else {
            // 電話改變：查找是否已有此電話的客戶，否則建立新客戶
            customer = customerRepository.findByPhone(newPhone).orElseGet(() ->
                    customerRepository.save(Customer.builder()
                            .name(request.getName())
                            .phone(newPhone)
                            .build()));
            if (!customer.getName().equals(request.getName())) {
                customer.setName(request.getName());
                customerRepository.save(customer);
            }
        }

        reservation.setCustomer(customer);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setGuestCount(request.getGuestCount());
        reservation.setNotes(request.getNotes());
        reservation.setRecommendedBy(request.getRecommendedBy());
        reservation.setStatus(ReservationStatus.PENDING);
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelPublicReservation(String token) {
        Reservation reservation = findPublicReservation(token);
        if (reservation.getStatus() == ReservationStatus.COMPLETED) throw new IllegalStateException("Completed reservation cannot be cancelled");
        reservation.setStatus(ReservationStatus.CANCELLED);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation reservation = findReservation(id);
        reservation.setEmployee(findEmployee(request.getEmployeeId()));
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setGuestCount(request.getGuestCount());
        reservation.setRecommendedBy(request.getRecommendedBy());
        reservation.setNotes(request.getNotes());
        if (request.getSource() != null) reservation.setSource(request.getSource());
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = findReservation(id);
        ReservationStatus previous = reservation.getStatus();
        reservation.setStatus(status);
        if (status == ReservationStatus.NO_SHOW && previous != ReservationStatus.NO_SHOW) {
            Customer customer = reservation.getCustomer();
            customer.setNoShowCount(customer.getNoShowCount() + 1);
        }
        return toResponse(reservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.delete(findReservation(id));
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    private Reservation findPublicReservation(String token) {
        return reservationRepository.findByPublicToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    private Employee findEmployee(Long id) {
        if (id == null) return null;
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    private void validateReservationTime(LocalTime time) {
        // Allowed: 15:00 ~ 23:59 and 00:00
        if (time == null) return; // @NotNull on DTO handles null
        LocalTime openTime = LocalTime.of(15, 0);
        if (!time.equals(LocalTime.MIDNIGHT) && time.isBefore(openTime)) {
            throw new IllegalArgumentException("訂位時間僅限 15:00 至 00:00，請重新選擇。");
        }
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .publicToken(reservation.getPublicToken())
                .customerId(reservation.getCustomer().getId())
                .customerName(reservation.getCustomer().getName())
                .customerPhone(reservation.getCustomer().getPhone())
                .employeeId(reservation.getEmployee() == null ? null : reservation.getEmployee().getId())
                .employeeName(reservation.getEmployee() == null ? null : reservation.getEmployee().getName())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .guestCount(reservation.getGuestCount())
                .status(reservation.getStatus())
                .recommendedBy(reservation.getRecommendedBy())
                .notes(reservation.getNotes())
                .source(reservation.getSource())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
