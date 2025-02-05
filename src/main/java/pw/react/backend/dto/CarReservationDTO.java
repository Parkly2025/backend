package pw.react.backend.dto;

import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

public record CarReservationDTO(@Email String userEmail, long carId, LocalDateTime startTime,
                                LocalDateTime endTime) {

}
