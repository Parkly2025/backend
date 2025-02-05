package pw.react.backend.dto;

import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReturnReservationDTO(Long id, Long parkingSpotId, Long userId, LocalDateTime startTime,
                                   LocalDateTime endTime, BigDecimal totalCost, LocalDateTime createdAt) {
    public static ReturnReservationDTO fromModel(Reservation reservation) {
        return new ReturnReservationDTO(reservation.getId(), reservation.getParkingSpot().getId(),
                reservation.getUser().getId(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getTotalCost(), reservation.getCreatedAt());
    }
}

