package pw.react.backend.dto;

import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.ParkingSpot;

public record CreateParkingSpotDTO(String spotNumber, Long parkingAreaId, boolean isAvailable) {
    public static CreateParkingSpotDTO fromModel(ParkingSpot parkingSpot) {
        return new CreateParkingSpotDTO(parkingSpot.getSpotNumber(), parkingSpot.getParkingArea().getId(),
                parkingSpot.getIsAvailable());
    }

    public ParkingSpot toModel(ParkingArea parkingArea) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setSpotNumber(spotNumber);
        parkingSpot.setParkingArea(parkingArea);
        parkingSpot.setIsAvailable(isAvailable);
        return parkingSpot;
    }
}
