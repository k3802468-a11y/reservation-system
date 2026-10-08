package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.statistics.StatisticsResponse;
import com.reservation.vor.reservation_system.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class StatisticsService {


    private final StatisticsRepository statisticsRepository;



    public StatisticsResponse getTodayStatistics(){


        LocalDate today = LocalDate.now();



        Long total =
                statisticsRepository.countTodayReservations(today);



        Long completed =
                statisticsRepository.countCompleted(today);



        Long noShow =
                statisticsRepository.countNoShow(today);



        Long cancelled =
                statisticsRepository.countCancelled(today);



        Long newCustomers =
                statisticsRepository.countNewCustomers();



        Long returningCustomers =
                statisticsRepository.countReturningCustomers();



        Double noShowRate = 0.0;

        if(total > 0){

            noShowRate =
                    (double) noShow / total * 100;

        }



        Double cancelRate = 0.0;

        if(total > 0){

            cancelRate =
                    (double) cancelled / total * 100;

        }



        Double returningRate = 0.0;

        long customerTotal =
                newCustomers + returningCustomers;


        if(customerTotal > 0){

            returningRate =
                    (double) returningCustomers
                            / customerTotal
                            * 100;

        }



        return StatisticsResponse.builder()

                .totalReservations(total)

                .completedCount(completed)

                .noShowCount(noShow)

                .cancelledCount(cancelled)

                .noShowRate(noShowRate)

                .cancelRate(cancelRate)

                .newCustomerCount(newCustomers)

                .returningCustomerCount(returningCustomers)

                .returningRate(returningRate)

                .build();

    }

}
