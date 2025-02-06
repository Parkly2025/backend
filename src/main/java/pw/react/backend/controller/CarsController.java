package pw.react.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.dto.CarReservationDTO;
import pw.react.backend.dto.CarsDTO;
import pw.react.backend.models.Car;
import pw.react.backend.services.ReservationService;
import pw.react.backend.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Cars", description = "Operations related to cars and their reservations")
public class CarsController {
    final private ReservationService reservationService;
    final private String carlyHostname;

    public CarsController(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.carlyHostname = System.getenv("CARLY_HOSTNAME");
    }

    @GetMapping("/search/{page}")
    @Operation(summary = "Get cars by proximity (longitude and latitude)",
            description = "Retrieves a paginated list of cars. Requires user or admin role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of cars", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges")
    })
    public ResponseEntity<?> getCarsByProximity(
            @Parameter(description = "Page number (0-based)", required = true, example = "0") @PathVariable int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(value = "sortDirection", required = false, defaultValue = "asc") String sortDirection,
            @Parameter(description = "Longitude", example = "12.324") @RequestParam(value = "long", required = true) double longitude,
            @Parameter(description = "Latitude", example = "12.324") @RequestParam(value = "lat", required = true) double latitude) {

        int tries = 5;
        while (tries > 0) {
            HttpClient client = HttpClient.newBuilder().build();
            int page_ = 1;
            int size_ = Integer.MAX_VALUE;
            String sort = "asc";

            String urlWithParams = String.format("%s/cars?page=%d&size=%d&sort=%s", carlyHostname, page_, size_, sort);

            Logger.getAnonymousLogger().log(new LogRecord(Level.WARNING, "Carly at: " + urlWithParams));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlWithParams))
                    .GET()
                    .build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String jsonString = response.body();

                ObjectMapper objectMapper = new ObjectMapper();

                CarsDTO carResponse = objectMapper.readValue(jsonString, CarsDTO.class);

                List<Car> cars = getCarList(longitude, latitude, carResponse);

                return ResponseEntity.status(HttpStatus.OK).body(convertListToPage(cars, page, size, sort));
            }
            catch (InterruptedException | IOException e) {
                Logger.getAnonymousLogger().log(new LogRecord(Level.WARNING, "Could not get to Carly:\n" + e.getMessage()));
            }
            tries--;
        }

        return ResponseEntity.internalServerError().build();
    }


    private static List<Car> getCarList(double longitude, double latitude, CarsDTO carResponse) {
        List<Car> cars = carResponse.content;

        // Sort the cars based on distance to the target
        cars.sort(new Comparator<Car>() {
            @Override
            public int compare(Car car1, Car car2) {
                double dist1 = Utils.haversine(car1.getLocation().getLatitude(), car1.getLocation().getLongitude(), latitude, longitude);
                double dist2 = Utils.haversine(car2.getLocation().getLatitude(), car2.getLocation().getLongitude(), latitude, longitude);
                return Double.compare(dist1, dist2); // Ascending order (closest first)
            }
        });
        return cars;
    }


    private static Page<Car> convertListToPage(List<Car> carList, int page, int size, String sortDirection) {
        Sort sort = Sort.by("model.name");
        if (sortDirection.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else if (sortDirection.equalsIgnoreCase("asc")) {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);


        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Car> pageContent;
        if (carList.size() < startItem) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, carList.size());
            pageContent = carList.subList(startItem, toIndex);
        }

        long total = carList.size(); // Total number of elements (important for pagination)
        return new PageImpl<>(pageContent, pageable, total);
    }

    @PostMapping
    @Operation(summary = "Create a new car reservation",
            description = "Creates a new car reservation. Requires admin or user role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "500", description = "Some kind of error occurred :(")
    })
    public ResponseEntity<?> createCarReservation(
            @Parameter(description = "Car reservation object DTO to create", required = true, schema = @Schema(implementation = CarReservationDTO.class)) @RequestBody CarReservationDTO reservationDTO
    ) {

        // Try log in the user to Carly fist:
        int resp = loginToCarly(reservationDTO.userEmail());

        switch (resp) {
            case 200:
                // successful login
                if (createCarlyCarReservation(reservationDTO.carId(), reservationDTO.startTime(),
                        reservationDTO.endTime(), reservationDTO.userEmail())) {
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            case 409:
                // we have to create user
                if (createCarlyUser(reservationDTO.userEmail())) {
                    if (createCarlyCarReservation(reservationDTO.carId(), reservationDTO.startTime(),
                            reservationDTO.endTime(), reservationDTO.userEmail())) {
                        return ResponseEntity.status(HttpStatus.CREATED).build();
                    }
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    /// Returns true if user created
    private Boolean createCarlyUser(String email) {
        HttpClient client = HttpClient.newBuilder().build();
        String urlWithParams = String.format("%s/customers/external", carlyHostname);
        String requestBody = "{\"email\": \"%s\"}".formatted(email);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // created user (hopefully)
            return response.statusCode() == HttpStatus.OK.value();

        } catch (InterruptedException | IOException e) {
            Logger.getAnonymousLogger().log(new LogRecord(Level.WARNING, "Could not get to Carly:\n" + e.getMessage()));
            return false;
        }
    }

    /// Returns status code from Carly server upon login try
    private int loginToCarly(String email) {
        HttpClient client = HttpClient.newBuilder().build();
        String urlWithParams = String.format("%s/customers/login", carlyHostname);
        String requestBody = "{\"email\": \"%s\"}".formatted(email);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        }
        catch (InterruptedException | IOException e) {
            Logger.getAnonymousLogger().log(new LogRecord(Level.WARNING, "Could not get to Carly:\n" + e.getMessage()));
            return 500;
        }
    }

    /// Returns true if reservation created
    private Boolean createCarlyCarReservation(long carId, LocalDateTime startDate, LocalDateTime endDate, String email)
    {
        HttpClient client = HttpClient.newBuilder().build();
        String urlWithParams = String.format("%s/rentals", carlyHostname);
        String requestBody = "{\"carId\": \"%d\", \"startAt\": \"%s\", \"endAt\": \"%s\"}".formatted(carId, startDate, endDate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithParams))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + email)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return HttpStatus.OK.value() == response.statusCode();
        }
        catch (InterruptedException | IOException e) {
            Logger.getAnonymousLogger().log(new LogRecord(Level.WARNING, "Could not get to Carly:\n" + e.getMessage()));
            return false;
        }
    }
}
