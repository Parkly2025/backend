package pw.react.backend.dto;

import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.ParkingSpot;

public record ReturnParkingSpotDTO(Long Id, String spotNumber, Long parkingAreaId, boolean isAvailable) {
    public static ReturnParkingSpotDTO fromModel(ParkingSpot parkingSpot) {
        return new ReturnParkingSpotDTO(parkingSpot.getId(), parkingSpot.getSpotNumber(), parkingSpot.getParkingArea().getId(),
                parkingSpot.isAvailable());
    }
}
