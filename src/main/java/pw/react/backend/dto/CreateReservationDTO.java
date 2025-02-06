package pw.react.backend.dto;

import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public record CreateReservationDTO(Long parkingSpotId, Long userId, LocalDateTime startTime,
                                   LocalDateTime endTime, Optional<BigDecimal> totalCost) {
    public static CreateReservationDTO fromModel(Reservation reservation) {
        return new CreateReservationDTO(reservation.getParkingSpot().getId(),
                reservation.getUser().getId(), reservation.getStartTime(),
                reservation.getEndTime(), Optional.of(reservation.getTotalCost()));
    }

    public Reservation toModel(ParkingSpot parkingSpot, User user) {
        Reservation reservation = new Reservation();
        reservation.setParkingSpot(parkingSpot);
        reservation.setUser(user);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        return reservation;
    }
}
