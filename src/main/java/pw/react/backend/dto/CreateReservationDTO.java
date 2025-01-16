package pw.react.backend.dto;

import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateReservationDTO(Long parkingSpotId, Long userId, LocalDateTime startTime,
                                   LocalDateTime endTime, BigDecimal totalCost) {
    public static CreateReservationDTO fromModel(Reservation reservation) {
        return new CreateReservationDTO(reservation.getParkingSpot().getId(),
                reservation.getUser().getId(), reservation.getStartTime(),
                reservation.getEndTime(), reservation.getTotalCost());
    }

    public Reservation toModel(ParkingSpot parkingSpot, User user) {
        Reservation reservation = new Reservation();
        reservation.setParkingSpot(parkingSpot);
        reservation.setUser(user);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalCost(totalCost);
        return reservation;
    }
}
