package pw.react.backend.dto;

import pw.react.backend.models.ParkingArea;

import java.math.BigDecimal;

public record CreateParkingAreaDTO(String name, String address, String city, BigDecimal hourlyRate, BigDecimal longitude, BigDecimal latitude) {
    public static CreateParkingAreaDTO fromModel(ParkingArea parkingArea) { return new CreateParkingAreaDTO(parkingArea.getName(), parkingArea.getAddress(), parkingArea.getCity(), parkingArea.getHourlyRate(), parkingArea.getLongitude(), parkingArea.getLatitude()); }

    public ParkingArea toModel() {
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setAddress(address);
        parkingArea.setCity(city);
        parkingArea.setName(name);
        parkingArea.setHourlyRate(hourlyRate);
        parkingArea.setLongitude(longitude);
        parkingArea.setLatitude(latitude);

        return parkingArea;
    }
}
