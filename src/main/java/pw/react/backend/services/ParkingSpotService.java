package pw.react.backend.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pw.react.backend.dto.CreateParkingSpotDTO;
import pw.react.backend.models.ParkingSpot;

import java.util.List;
import java.util.Optional;

@Service
public interface ParkingSpotService {
    Page<ParkingSpot> getParkingSpots(Pageable pageable);
    Optional<ParkingSpot> getParkingSpot(Long id);
    ParkingSpot createParkingSpot(CreateParkingSpotDTO parkingSpotDTO);
    Optional<ParkingSpot> updateParkingSpot(Long id, ParkingSpot parkingSpot);
    Boolean deleteParkingSpot(Long id);
    List<ParkingSpot> getParkingSpotsByParkingAreaId(Long parkingAreaId);
}
