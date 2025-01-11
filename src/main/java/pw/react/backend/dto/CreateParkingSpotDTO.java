package pw.react.backend.dto;

import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.ParkingSpot;

import java.math.BigDecimal;

public record CreateParkingSpotDTO(String spotNumber, Long parkingAreaId, boolean isAvailable) {
    public static CreateParkingSpotDTO fromModel(ParkingSpot parkingSpot) {
        return new CreateParkingSpotDTO(parkingSpot.getSpotNumber(), parkingSpot.getParkingArea().getId(),
                parkingSpot.isAvailable());
    }

    public static ParkingSpot toModel(CreateParkingSpotDTO parkingSpotDTO, ParkingArea parkingArea) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setSpotNumber(parkingSpotDTO.spotNumber());
        parkingSpot.setParkingArea(parkingArea);
        parkingSpot.setAvailable(parkingSpot.isAvailable());
        return parkingSpot;
    }
}
