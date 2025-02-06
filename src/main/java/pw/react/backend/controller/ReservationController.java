package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dto.CreateReservationDTO;
import pw.react.backend.dto.ReturnReservationDTO;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.Reservation;
import pw.react.backend.services.ReservationService;
import pw.react.backend.utils.Utils;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations (pure Parkly)", description = "Operations related to reservations")
public class ReservationController {

    final private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/page/{page}")
    @Operation(summary = "Get all reservations",
            description = "Retrieves a paginated list of reservations. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservations", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> getAllReservations(
            @Parameter(description = "Page number (0-based)", required = true, example = "0") @PathVariable int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(reservationService.findAll(page, size, sortDirection).map(ReturnReservationDTO::fromModel));
    }


    @GetMapping("/user/{id}/page/{page}")
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
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection)
    {

        return ResponseEntity.ok(reservationService.findByUserId(page, size, sortDirection, id).map(ReturnReservationDTO::fromModel));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID",
            description = "Retrieves a reservation by its ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Not Found - reservation not found")
    })
    public ResponseEntity<?> getReservationById(
            @Parameter(description = "ID of the reservation to retrieve", required = true, example = "123") @PathVariable Long id) {

        return reservationService.findById(id)
                .map(reservation -> ResponseEntity.ok(ReturnReservationDTO.fromModel(reservation))) // Convert to DTO here
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/parkingSpot/{id}")
    @Operation(summary = "Get reservation by Parking Spot ID",
            description = "Retrieves a reservation by its Parking Spot ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of reservation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Not Found - reservation not found")
    })
    public ResponseEntity<?> getReservationByParkingSpotId(
            @Parameter(description = "ID of the Parking Spot reservation to retrieve", required = true, example = "123") @PathVariable Long id) {

        return reservationService.findByParkingSpotId(id)
                .map(reservation -> ResponseEntity.ok(ReturnReservationDTO.fromModel(reservation))) // Convert to DTO here
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @Operation(summary = "Create a new reservation",
            description = "Creates a new reservation. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))), // Text plain for error message
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> createReservation(
            @Parameter(description = "Reservation object DTO to create", required = true, schema = @Schema(implementation = CreateReservationDTO.class)) @RequestBody CreateReservationDTO reservationDTO) {


        try {
            Reservation createdReservation = reservationService.create(reservationDTO);
            return ResponseEntity.ok(ReturnReservationDTO.fromModel(createdReservation));
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation",
            description = "Deletes a reservation by its ID. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))), // Text plain for error message
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> deleteReservation(
            @Parameter(description = "ID of the reservation to delete", required = true, example = "123") @PathVariable Long id) {

        try {
            reservationService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reservation",
            description = "Updates an existing reservation. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnReservationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid input data or validation errors", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> updateReservation(
            @Parameter(description = "ID of the reservation to update", required = true, example = "123") @PathVariable Long id,
            @Parameter(description = "Reservation object DTO to update", required = true, schema = @Schema(implementation = CreateReservationDTO.class)) @RequestBody CreateReservationDTO reservationDTO) {

        try {
            Reservation reservation = reservationService.update(id, reservationDTO);
            return ResponseEntity.ok().body(ReturnReservationDTO.fromModel(reservation)); // Convert to DTO
        } catch (ModelValidationException | ModelNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}