package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dto.CreateParkingSpotDTO;
import pw.react.backend.dto.ReturnParkingSpotDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.services.ParkingSpotService;
import pw.react.backend.utils.ProtectedEndpoint;
import pw.react.backend.utils.Utils;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-spots")
@Tag(name = "Parking Spots", description = "Operations related to parking spots")
public class ParkingSpotController {

    final private ParkingSpotService parkingSpotService;

    ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @Operation(summary = "Get all parking spots paginated", description = "Retrieves a paginated list of parking spots.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of parking spots",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid page number or size")
    })
    @GetMapping("/page/{page}")
    public Page<ReturnParkingSpotDTO> getAllParkingSpots(
            @Parameter(description = "Page number (0-based)", required = true) @PathVariable int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(value = "sortBy", required = false, defaultValue = "spotNumber") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = Sort.by(sortBy);
        if (sortDirection.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else if (sortDirection.equalsIgnoreCase("asc")) {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return parkingSpotService.getParkingSpots(pageable).map(ReturnParkingSpotDTO::fromModel);
    }

    @Operation(summary = "Get all parking spots by Parking Area id", description = "Retrieves a list of parking spots of particular Parking area.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of parking spots",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Parking area id")
    })
    @GetMapping("/pa")
    public ResponseEntity<?> getAllParkingSpots(
            @Parameter(description = "Parking Area id") @RequestParam(value = "paId", required = true) Long paId)
    {
        try {
            return ResponseEntity.ok(parkingSpotService.getParkingSpotsByParkingAreaId(paId).stream().map(ReturnParkingSpotDTO::fromModel));
        } catch (ModelValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get parking spot by ID", description = "Retrieves a parking spot by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of parking spot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingSpot.class))),
            @ApiResponse(responseCode = "404", description = "Parking spot not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getParkingSpotById(@Parameter(description = "ID of the parking spot to retrieve", required = true) @PathVariable Long id) {
        Optional<ParkingSpot> ps = parkingSpotService.getParkingSpot(id);
        if (ps.isPresent()) {
            return ResponseEntity.ok(ReturnParkingSpotDTO.fromModel(ps.get()));
        }
        return ResponseEntity.notFound().build();

    }

    @Operation(summary = "Create a new parking spot", description = "Creates a new parking spot.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking spot created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnParkingSpotDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Parking spot already exists or validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))) //Schema for the error message
    })
    @PostMapping
    @ProtectedEndpoint
    public ResponseEntity<?> createParkingSpot(@Parameter(description = "Parking spot DTO object to create", required = true, schema = @Schema(implementation = CreateParkingSpotDTO.class)) @RequestBody CreateParkingSpotDTO createParkingSpotDTO,
                                               @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdmin(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            ParkingSpot savedParkingSpot = parkingSpotService.createParkingSpot(createParkingSpotDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ReturnParkingSpotDTO.fromModel(savedParkingSpot));
        } catch (ModelAlreadyExistsException | ModelValidationException e ) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update an existing parking spot", description = "Updates an existing parking spot by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spot updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnParkingSpotDTO.class))),
            @ApiResponse(responseCode = "404", description = "Parking spot not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Validation errors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<?> updateParkingSpot(@Parameter(description = "ID of the parking spot to update", required = true) @PathVariable Long id, @Parameter(description = "Updated parking spot object", required = true, schema = @Schema(implementation = ParkingSpot.class)) @RequestBody ParkingSpot updatedParkingSpot,
                                               @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdminOrUser(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Optional<ParkingSpot> ps = parkingSpotService.updateParkingSpot(id, updatedParkingSpot);
        if (ps.isPresent()) {
            return ResponseEntity.ok(ReturnParkingSpotDTO.fromModel(ps.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete a parking spot", description = "Deletes a parking spot by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parking spot deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Parking spot not found")
    })
    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    public ResponseEntity<Void> deleteParkingSpot(@Parameter(description = "ID of the parking spot to delete", required = true) @PathVariable Long id,
                                                  @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdmin(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (parkingSpotService.deleteParkingSpot(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
