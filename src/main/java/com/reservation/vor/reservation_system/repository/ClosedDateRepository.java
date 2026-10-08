package com.reservation.vor.reservation_system.repository;

import com.reservation.vor.reservation_system.entity.ClosedDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClosedDateRepository extends JpaRepository<ClosedDate, Long> {
    boolean existsByDate(LocalDate date);

    Optional<ClosedDate> findByDate(LocalDate date);

    List<ClosedDate> findAllByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    List<ClosedDate> findAllByOrderByDateAsc();

    void deleteByDate(LocalDate date);
}
