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
import pw.react.backend.dto.ReturnReservationDTO;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;
import pw.react.backend.services.ReservationService;
import pw.react.backend.utils.ProtectedEndpoint;
import pw.react.backend.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations (pure Parkly)", description = "Operations related to reservations")
public class ReservationController {

    final private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/page/{page}")
    @ProtectedEndpoint
    @Operation(summary = "Get all reservations",
            description = "Retrieves a paginated list of reservations. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservations", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> getAllReservations(
            @Parameter(description = "Page number (0-based)", required = true, example = "0") @PathVariable int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @Parameter @CookieValue(value = "userRole", required = false) String userRole) {


        if (!Utils.roleAdmin(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reservationService.findAll(page, size, sortDirection).map(ReturnReservationDTO::fromModel));
    }


    @GetMapping("/user/{id}/page/{page}")
    @ProtectedEndpoint
    @Operation(summary = "Get reservations for a specific user",
            description = "Retrieves a paginated list of reservations for a given user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservations", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input parameters (e.g., invalid user ID, page number out of range)"),
            @ApiResponse(responseCode = "400", description = "Forbidden - requires either Admin or User role"),
    })
    public ResponseEntity<?> getAllUserReservations(
            @Parameter(description = "User ID", required = true, example = "123") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", required = true, example = "0") @PathVariable int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdminOrUser(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reservationService.findByUserId(page, size, sortDirection, id).map(ReturnReservationDTO::fromModel));
    }

    @GetMapping("/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Get reservation by ID",
            description = "Retrieves a reservation by its ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Not Found - reservation not found")
    })
    public ResponseEntity<?> getReservationById(
            @Parameter(description = "ID of the reservation to retrieve", required = true, example = "123") @PathVariable Long id,
            @Parameter @CookieValue(value = "userRole", required = false) String userRole) {

        if (!Utils.roleAdminOrUser(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return reservationService.findById(id)
                .map(reservation -> ResponseEntity.ok(ReturnReservationDTO.fromModel(reservation))) // Convert to DTO here
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/parkingSpot/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Get reservation by Parking Spot ID",
            description = "Retrieves a reservation by its Parking Spot ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Not Found - reservation not found")
    })
    public ResponseEntity<?> getReservationByParkingSpotId(
            @Parameter(description = "ID of the Parking Spot reservation to retrieve", required = true, example = "123") @PathVariable Long id,
            @Parameter @CookieValue(value = "userRole", required = false) String userRole) {

        if (!Utils.roleAdminOrUser(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return reservationService.findByParkingSpotId(id)
                .map(reservation -> ResponseEntity.ok(ReturnReservationDTO.fromModel(reservation))) // Convert to DTO here
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ProtectedEndpoint
    @Operation(summary = "Create a new reservation",
            description = "Creates a new reservation. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))), // Text plain for error message
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> createReservation(
            @Parameter(description = "Reservation object DTO to create", required = true, schema = @Schema(implementation = CreateReservationDTO.class)) @RequestBody CreateReservationDTO reservationDTO,
            @Parameter @CookieValue(value = "userRole", required = false) String userRole) {

        if (!Utils.roleAdminOrUser(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Reservation createdReservation = reservationService.create(reservationDTO);
            return ResponseEntity.ok(ReturnReservationDTO.fromModel(createdReservation));
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Delete a reservation",
            description = "Deletes a reservation by its ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))), // Text plain for error message
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> deleteReservation(
            @Parameter(description = "ID of the reservation to delete", required = true, example = "123") @PathVariable Long id,
            @Parameter(hidden = true) @CookieValue(value = "userRole", required = false) String userRole) {

        if (!Utils.roleAdminOrUser(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            reservationService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Update a reservation",
            description = "Updates an existing reservation. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> updateReservation(
            @Parameter(description = "ID of the reservation to update", required = true, example = "123") @PathVariable Long id,
            @Parameter(description = "Reservation object DTO to update", required = true, schema = @Schema(implementation = CreateReservationDTO.class)) @RequestBody CreateReservationDTO reservationDTO,
            @Parameter(hidden = true) @CookieValue(value = "userRole", required = false) String userRole) {

        if (!Utils.roleAdminOrUser(userRole)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Reservation reservation = reservationService.update(id, reservationDTO);
            return ResponseEntity.ok().body(ReturnReservationDTO.fromModel(reservation)); // Convert to DTO
        } catch (ModelValidationException | ModelNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}