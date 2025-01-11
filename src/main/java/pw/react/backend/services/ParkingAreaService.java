package pw.react.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.models.ParkingArea;

import java.util.Optional;

@Service
public interface ParkingAreaService {
    Page<ParkingArea> getParkingAreas(int page, int size, String sortDirection,
                                      String searchQuery, String searchQueryParameter);
    Optional<ParkingArea> getParkingArea(Long id);
    ParkingArea createParkingArea(ParkingArea parkingArea) throws ModelAlreadyExistsException;
    Optional<ParkingArea> updateParkingArea(Long id, ParkingArea parkingArea);
    Boolean deleteParkingArea(Long id);
}
