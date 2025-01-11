package pw.react.backend.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
public class ParkingArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int numParkingSpots;

    @Column(nullable = false)
    private int maxNumParkingSpots;

    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getNumParkingSpots() { return numParkingSpots; }
    public void setNumParkingSpots(int num) { this.numParkingSpots = num; }
    public int getMaxNumParkingSpots() { return maxNumParkingSpots; }
    public void setMaxNumParkingSpots(int max) { this.maxNumParkingSpots = max; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
}

