package com.reservation.vor.reservation_system.dto.statistics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsResponse {


    // 總預約數
    private Long totalReservations;


    // 完成數
    private Long completedCount;


    // No Show數
    private Long noShowCount;


    // 取消數
    private Long cancelledCount;


    // No Show比例
    private Double noShowRate;


    // 取消比例
    private Double cancelRate;


    // 新客數
    private Long newCustomerCount;


    // 老客數
    private Long returningCustomerCount;


    // 回訪率
    private Double returningRate;

}