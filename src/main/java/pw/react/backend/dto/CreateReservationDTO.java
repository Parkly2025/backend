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

    public static Reservation toModel(CreateReservationDTO createReservationDTO, ParkingSpot parkingSpot, User user) {
        Reservation reservation = new Reservation();
        reservation.setParkingSpot(parkingSpot);
        reservation.setUser(user);
        reservation.setStartTime(createReservationDTO.startTime());
        reservation.setEndTime(createReservationDTO.endTime());
        reservation.setTotalCost(createReservationDTO.totalCost());
        return reservation;
    }
}
