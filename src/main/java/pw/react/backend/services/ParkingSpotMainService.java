package pw.react.backend.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pw.react.backend.dao.ParkingAreaRepository;
import pw.react.backend.dao.ParkingSpotRepository;
import pw.react.backend.dto.CreateParkingAreaDTO;
import pw.react.backend.dto.CreateParkingSpotDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.ParkingSpot;

import java.util.List;
import java.util.Optional;

@Service
@Qualifier("parkingSpotService")
public class ParkingSpotMainService implements ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingAreaRepository parkingAreaRepository;

    public ParkingSpotMainService(ParkingSpotRepository parkingSpotRepository, ParkingAreaRepository parkingAreaRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingAreaRepository = parkingAreaRepository;
    }

    @Override
    public Page<ParkingSpot> getParkingSpots(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }

    @Override
    public Optional<ParkingSpot> getParkingSpot(Long id) {
        if (parkingSpotRepository.findById(id).isPresent()) {
            return parkingSpotRepository.findById(id);
        }
        return Optional.empty();
    }

    @Override
    public ParkingSpot createParkingSpot(CreateParkingSpotDTO parkingSpotDTO) throws ModelAlreadyExistsException, ModelValidationException {
        Optional<ParkingArea> parkingArea = parkingAreaRepository.findById(parkingSpotDTO.parkingAreaId());
        if (parkingArea.isEmpty())
        {
            throw new ModelValidationException("Parking spot is required and must exist");
        }
        ParkingArea pa = parkingArea.get();
        if (parkingSpotRepository.existsByParkingAreaAndSpotNumber(pa, parkingSpotDTO.spotNumber())) {
            throw new ModelAlreadyExistsException("A parking spot with the specified spot number already exists");
        }
        return parkingSpotRepository.save(CreateParkingSpotDTO.toModel(parkingSpotDTO, pa));
    }

    @Override
    public Optional<ParkingSpot> updateParkingSpot(Long id, ParkingSpot parkingSpot) {
        if (parkingSpotRepository.findById(id).isPresent()) {
            ParkingSpot ps = new ParkingSpot();
            ps.setAvailable(parkingSpot.isAvailable());
            ps.setSpotNumber(parkingSpot.getSpotNumber());
            ps.setParkingArea(parkingSpot.getParkingArea());
            ps = parkingSpotRepository.save(ps);
            return Optional.of(ps);
        }
        return Optional.empty();
    }

    @Override
    public Boolean deleteParkingSpot(Long id) {
        if (parkingSpotRepository.findById(id).isPresent()) {
            parkingSpotRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<ParkingSpot> getParkingSpotsByParkingAreaId(Long parkingAreaId) {
        if (parkingAreaRepository.existsById(parkingAreaId)) {
            return parkingSpotRepository.findParkingSpotsByParkingAreaId(parkingAreaId);
        }
        throw new ModelValidationException("Parking area with id " + parkingAreaId + " not found");
    }
}
