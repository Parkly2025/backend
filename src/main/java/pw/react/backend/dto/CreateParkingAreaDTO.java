package pw.react.backend.dto;

import pw.react.backend.models.ParkingArea;

import java.math.BigDecimal;

public record CreateParkingAreaDTO(String name, String address, String city, int maxNumParkingSpots, int numParkingSpots, BigDecimal hourlyRate) {
    public static CreateParkingAreaDTO fromModel(ParkingArea parkingArea) { return new CreateParkingAreaDTO(parkingArea.getName(), parkingArea.getAddress(), parkingArea.getCity(), parkingArea.getMaxNumParkingSpots(), parkingArea.getNumParkingSpots(), parkingArea.getHourlyRate()); }

    public static ParkingArea toModel(CreateParkingAreaDTO createParkingAreaDTO) {
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setAddress(createParkingAreaDTO.address);
        parkingArea.setCity(createParkingAreaDTO.city);
        parkingArea.setName(createParkingAreaDTO.name);
        parkingArea.setMaxNumParkingSpots(createParkingAreaDTO.maxNumParkingSpots);
        parkingArea.setNumParkingSpots(createParkingAreaDTO.numParkingSpots);
        parkingArea.setHourlyRate(createParkingAreaDTO.hourlyRate);

        return parkingArea;
    }
}
