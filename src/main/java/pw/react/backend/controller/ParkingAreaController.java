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
import pw.react.backend.dto.CreateParkingAreaDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.services.ParkingAreaService;
import pw.react.backend.utils.ProtectedEndpoint;
import pw.react.backend.utils.Utils;

import java.util.Optional;


@RestController
@RequestMapping("/api/parking-areas")
@Tag(name = "Parking areas", description = "Operations related to parking areas")
public class ParkingAreaController {

    final private ParkingAreaService parkingAreaService;

    ParkingAreaController(ParkingAreaService parkingAreaService) {
        this.parkingAreaService = parkingAreaService;
    }


    @GetMapping("/page/{page}")
    @Operation(summary = "Get all parking areas (paginated)", description = "Retrieves a paginated list of parking areas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of parking areas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public Page<ParkingArea> getAllParkingAreas(
            @Parameter(description = "Page number (0-based)", required = true) @PathVariable int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @Parameter(description = "Search query") @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @Parameter(description = "Search query property / parameter. Possible values: ['address', 'city', 'name']") @RequestParam(value = "searchQueryParameter", required = false) String searchQueryParameter) {
        return parkingAreaService.getParkingAreas(page, size, sortDirection,
                searchQuery, searchQueryParameter);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get parking area by ID", description = "Retrieves a parking area by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking area found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingArea.class))),
            @ApiResponse(responseCode = "404", description = "Parking area not found")
    })
    public ResponseEntity<?> getParkingAreaById(@Parameter(description = "ID of the parking area to retrieve", required = true) @PathVariable Long id) {
        return parkingAreaService.getParkingArea(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ProtectedEndpoint
    @Operation(summary = "Create a new parking area", description = "Creates a new parking area. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking area created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingArea.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Parking area already exists or invalid data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin role required)")
    })
    public ResponseEntity<?> createParkingArea(
            @Parameter(description = "Parking area DTO object to create", required = true, schema = @Schema(implementation = CreateParkingAreaDTO.class)) @RequestBody CreateParkingAreaDTO parkingAreaDTO,
            @Parameter(description = "Cookie containing User role.") @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdmin(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            ParkingArea savedParkingArea = parkingAreaService.createParkingArea(parkingAreaDTO.toModel());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParkingArea);
        } catch (ModelAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Update a parking area", description = "Updates a parking area based on the provided ID and data. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking area updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingArea.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin role required)"),
            @ApiResponse(responseCode = "404", description = "Not Found - Parking area with the specified ID does not exist")
    })
    public ResponseEntity<?> updateParkingArea(
            @Parameter(description = "ID of the parking area to update", required = true) @PathVariable Long id,
            @Parameter(description = "Updated parking area object", required = true, schema = @Schema(implementation = ParkingArea.class)) @RequestBody ParkingArea updatedParkingArea,
            @Parameter(description = "Cookie containing User role.") @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdmin(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Optional<ParkingArea> pa = parkingAreaService.updateParkingArea(id, updatedParkingArea);
        return pa.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    @ProtectedEndpoint
    @Operation(summary = "Delete a parking area", description = "Deletes a parking area based on the provided ID. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parking area deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (Admin role required)"),
            @ApiResponse(responseCode = "404", description = "Not Found - Parking area with the specified ID does not exist")
    })
    public ResponseEntity<Void> deleteParkingArea(
            @Parameter(description = "ID of the parking area to delete", required = true) @PathVariable Long id,
            @Parameter(description = "Cookie containing User role.") @CookieValue(value = "userRole", required = false) String userRole) {
        if (!Utils.roleAdmin(userRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (parkingAreaService.deleteParkingArea(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
