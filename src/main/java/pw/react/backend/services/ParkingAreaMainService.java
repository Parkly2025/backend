package pw.react.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pw.react.backend.dao.ParkingAreaRepository;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.User;
import pw.react.backend.specifications.ParkingAreaSpecification;
import pw.react.backend.specifications.UserSpecification;

import java.util.Optional;

@Service
public class ParkingAreaMainService implements ParkingAreaService {

    private final ParkingAreaRepository parkingAreaRepository;

    public ParkingAreaMainService(ParkingAreaRepository parkingAreaRepository) {
        this.parkingAreaRepository = parkingAreaRepository;
    }

    @Override
    public Page<ParkingArea> getParkingAreas(int page, int size, String sortDirection,
                                             String searchQuery, String searchQueryParameter) {
        Sort sort = Sort.by("address");
        if (sortDirection.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else if (sortDirection.equalsIgnoreCase("asc")) {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        searchQuery = searchQuery == null ? "" : searchQuery;
        searchQueryParameter = searchQueryParameter == null ? "" : searchQueryParameter;
        Specification<ParkingArea> specification = switch (searchQueryParameter) {
            case "address" -> ParkingAreaSpecification.hasAddress(searchQuery);
            case "city" -> ParkingAreaSpecification.hasCity(searchQuery);
            case "name" -> ParkingAreaSpecification.hasName(searchQuery);
            default -> Specification.where(null);
        };

        return parkingAreaRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<ParkingArea> getParkingArea(Long id) {
        return parkingAreaRepository.findById(id);
    }

    @Override
    public ParkingArea createParkingArea(ParkingArea parkingArea) throws ModelAlreadyExistsException {
        if (parkingAreaRepository.existsByName(parkingArea.getName())) {
            throw new ModelAlreadyExistsException(parkingArea.getName());
        }
        return parkingAreaRepository.save(parkingArea);
    }

    @Override
    public Optional<ParkingArea> updateParkingArea(Long id, ParkingArea parkingArea) {
        if (parkingAreaRepository.findById(id).isPresent()) {
            ParkingArea pa = new ParkingArea();

            pa.setId(id);
            pa.setName(parkingArea.getName());
            pa.setAddress(parkingArea.getAddress());
            pa.setCity(parkingArea.getCity());
            pa.setHourlyRate(parkingArea.getHourlyRate());
            pa.setLongitude(parkingArea.getLongitude());
            pa.setLatitude(parkingArea.getLatitude());

            parkingAreaRepository.save(pa);
            return Optional.of(pa);
        }
        return Optional.empty();
    }

    @Override
    public Boolean deleteParkingArea(Long id) {
        if (parkingAreaRepository.findById(id).isPresent()) {
            parkingAreaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
