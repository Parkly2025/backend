package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dao.ParkingSpotRepository;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.dto.CreateParkingSpotDTO;
import pw.react.backend.dto.CreateReservationDTO;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;
import pw.react.backend.services.ReservationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations (NOT DONE!)", description = "Operations related to parking spot reservations")
public class ReservationController {

    final private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/page/{page}")
    public Page<Reservation> getAllReservations(
            @Parameter(description = "Page number (0-based)", required = true) @PathVariable int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection) {
        return reservationService.findAll(size, page, sortDirection);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@Parameter(description = "ID of the reservation to retrieve", required = true) @PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?> createReservation(@Parameter(description = "Reservation object DTO to create", required = true, schema = @Schema(implementation = CreateReservationDTO.class)) @RequestBody CreateReservationDTO reservationDTO) {
        try {
            return ResponseEntity.ok(reservationService.create(reservationDTO));
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@Parameter(description = "ID of the reservation to delete", required = true) @PathVariable Long id) {
        try {
            reservationService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // TODO: PUT endpoint
}