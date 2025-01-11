package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.User;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long>, JpaSpecificationExecutor<ParkingSpot> {
    List<ParkingSpot> findByParkingArea(ParkingArea parkingArea);
    ParkingSpot findBySpotNumberAndParkingArea(String spotNumber, ParkingArea parkingArea);
    Boolean existsByParkingAreaAndSpotNumber(ParkingArea parkingArea, String spotNumber);
    List<ParkingSpot> findParkingSpotsByParkingAreaId(Long parkingAreaId);
}
