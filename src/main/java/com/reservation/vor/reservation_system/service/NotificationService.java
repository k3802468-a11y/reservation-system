package com.reservation.vor.reservation_system.service;

import com.reservation.vor.reservation_system.dto.notification.NotificationRequest;
import com.reservation.vor.reservation_system.dto.notification.NotificationResponse;
import com.reservation.vor.reservation_system.entity.Notification;
import com.reservation.vor.reservation_system.entity.Reservation;
import com.reservation.vor.reservation_system.repository.NotificationRepository;
import com.reservation.vor.reservation_system.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;

    private final ReservationRepository reservationRepository;



    // 查詢全部通知
    public List<NotificationResponse> getAllNotifications(){


        return notificationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

    }




    // 查詢某筆預約通知
    public List<NotificationResponse> getByReservation(
            Long reservationId
    ){

        return notificationRepository
                .findByReservationId(reservationId)
                .stream()
                .map(this::toResponse)
                .toList();

    }





    // 建立通知紀錄
    public NotificationResponse createNotification(
            NotificationRequest request
    ){


        Reservation reservation =
                reservationRepository.findById(
                                request.getReservationId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found"
                                )
                        );



        Notification notification =
                Notification.builder()
                        .reservation(reservation)
                        .channel(request.getChannel())
                        .status(
                                request.getStatus() != null
                                        ? request.getStatus()
                                        : "PENDING"
                        )
                        .build();



        notificationRepository.save(notification);



        return toResponse(notification);

    }





    // 更新通知狀態
    public NotificationResponse updateStatus(
            Long id,
            String status
    ){


        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );


        notification.setStatus(status);


        notificationRepository.save(notification);


        return toResponse(notification);

    }





    // Entity 轉 DTO
    private NotificationResponse toResponse(
            Notification notification
    ){


        return NotificationResponse.builder()

                .id(notification.getId())

                .reservationId(
                        notification.getReservation().getId()
                )

                .channel(
                        notification.getChannel()
                )

                .status(
                        notification.getStatus()
                )

                .sentAt(
                        notification.getSentAt()
                )

                .build();

    }

}