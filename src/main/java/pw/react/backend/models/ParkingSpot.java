package pw.react.backend.models;

import jakarta.persistence.*;

@Entity
@Table
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String spotNumber; // e.g., "A1", "B2"

    @ManyToOne
    @JoinColumn(name = "parkingAreaId", nullable = false)
    private ParkingArea parkingArea;

    @Column(nullable = false)
    private boolean isAvailable;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String spotNumber) { this.spotNumber = spotNumber; }
    public ParkingArea getParkingArea() { return parkingArea; }
    public void setParkingArea(ParkingArea parkingArea) {this.parkingArea = parkingArea;}
    public boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(boolean available) { this.isAvailable = available; }
}
