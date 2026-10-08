package com.reservation.vor.reservation_system.controller;

import com.reservation.vor.reservation_system.dto.statistics.StatisticsResponse;
import com.reservation.vor.reservation_system.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {


    private final StatisticsService statisticsService;



    // 今日統計
    @GetMapping("/today")
    public ResponseEntity<StatisticsResponse> getTodayStatistics(){


        return ResponseEntity.ok(
                statisticsService.getTodayStatistics()
        );

    }

}