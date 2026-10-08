package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.closure.ClosureSettingsResponse;
import com.reservation.vor.reservation_system.entity.ClosedDate;
import com.reservation.vor.reservation_system.entity.StoreSettings;
import com.reservation.vor.reservation_system.repository.ClosedDateRepository;
import com.reservation.vor.reservation_system.repository.StoreSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreClosureService {

    private final ClosedDateRepository closedDateRepository;
    private final StoreSettingsRepository storeSettingsRepository;

    private static final Long SETTINGS_ID = 1L;

    /**
     * Check if a specific date is closed (either regular weekly day off or ad-hoc closure).
     */
    public boolean isDateClosed(LocalDate date) {
        if (date == null) return false;

        // 1. Check regular weekly closed days
        int jsDayOfWeek = date.getDayOfWeek().getValue() % 7; // 0=Sun, 1=Mon, ..., 6=Sat
        List<Integer> regularDays = getRegularClosedDays();
        if (regularDays.contains(jsDayOfWeek)) {
            return true;
        }

        // 2. Check ad-hoc closed dates
        return closedDateRepository.existsByDate(date);
    }

    /**
     * Get list of regular closed days (0=Sunday, 1=Monday, ..., 6=Saturday).
     */
    public List<Integer> getRegularClosedDays() {
        return storeSettingsRepository.findById(SETTINGS_ID)
                .map(StoreSettings::getRegularClosedDays)
                .filter(str -> str != null && !str.trim().isEmpty())
                .map(str -> Arrays.stream(str.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Update regular closed days.
     */
    @Transactional
    public void setRegularClosedDays(List<Integer> days) {
        StoreSettings settings = storeSettingsRepository.findById(SETTINGS_ID)
                .orElse(StoreSettings.builder().id(SETTINGS_ID).build());

        if (days == null || days.isEmpty()) {
            settings.setRegularClosedDays("");
        } else {
            String daysStr = days.stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            settings.setRegularClosedDays(daysStr);
        }

        storeSettingsRepository.save(settings);
    }

    /**
     * Add an ad-hoc closure date.
     */
    @Transactional
    public ClosedDate addClosedDate(LocalDate date, String reason) {
        if (date == null) {
            throw new IllegalArgumentException("休假日期不能為空");
        }
        if (closedDateRepository.existsByDate(date)) {
            throw new IllegalArgumentException("該日期已經設定為休假日");
        }

        ClosedDate closedDate = ClosedDate.builder()
                .date(date)
                .reason(reason)
                .build();
        return closedDateRepository.save(closedDate);
    }

    /**
     * Remove an ad-hoc closure date.
     */
    @Transactional
    public void removeClosedDate(LocalDate date) {
        closedDateRepository.findByDate(date).ifPresent(closedDateRepository::delete);
    }

    /**
     * Remove an ad-hoc closure date by ID.
     */
    @Transactional
    public void removeClosedDateById(Long id) {
        closedDateRepository.deleteById(id);
    }

    /**
     * Get all closure settings (regular closed days + ad-hoc closed dates).
     */
    public ClosureSettingsResponse getClosureSettings() {
        List<Integer> regularDays = getRegularClosedDays();
        List<ClosedDate> closedDates = closedDateRepository.findAllByOrderByDateAsc();

        List<ClosureSettingsResponse.ClosedDateItem> items = closedDates.stream()
                .map(cd -> ClosureSettingsResponse.ClosedDateItem.builder()
                        .id(cd.getId())
                        .date(cd.getDate())
                        .reason(cd.getReason())
                        .build())
                .collect(Collectors.toList());

        return ClosureSettingsResponse.builder()
                .regularClosedDays(regularDays)
                .closedDates(items)
                .build();
    }

    /**
     * Get all closed dates in a range (for calendar disablement on front-end).
     */
    public List<LocalDate> getClosedDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<Integer> regularDays = getRegularClosedDays();
        Set<LocalDate> closedDates = new TreeSet<>();

        // Add regular days in range
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int jsDay = current.getDayOfWeek().getValue() % 7;
            if (regularDays.contains(jsDay)) {
                closedDates.add(current);
            }
            current = current.plusDays(1);
        }

        // Add ad-hoc closed dates in range
        List<ClosedDate> adHocDates = closedDateRepository.findAllByDateBetweenOrderByDateAsc(startDate, endDate);
        for (ClosedDate cd : adHocDates) {
            closedDates.add(cd.getDate());
        }

        return new ArrayList<>(closedDates);
    }
}
