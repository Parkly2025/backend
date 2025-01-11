package pw.react.backend.services;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.dto.CreateReservationDTO;
import pw.react.backend.exceptions.ModelAlreadyExistsException;
import pw.react.backend.exceptions.ModelNotFoundException;
import pw.react.backend.exceptions.ModelValidationException;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;

import java.util.Optional;

@Service
public class ReservationMainService implements ReservationService {

    final private ReservationRepository reservationRepository;
    private final UserService userService;
    private final ParkingSpotService parkingSpotService;

    public ReservationMainService(ReservationRepository reservationRepository, @Qualifier("userService") UserService userService, @Qualifier("parkingSpotService") ParkingSpotService parkingSpotService) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.parkingSpotService = parkingSpotService;
    }

    @Override
    public Page<Reservation> findAll(int page, int size, String sortDirection) {
        Sort sort = Sort.by("startTime", "endTime");
        if (sortDirection.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        } else if (sortDirection.equalsIgnoreCase("asc")) {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return reservationRepository.findAll(pageable);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Reservation create(CreateReservationDTO reservationDTO) throws ModelValidationException, ModelAlreadyExistsException {
        Optional<User> user = userService.findById(reservationDTO.userId());
        if (user.isEmpty()) {
            throw new ModelValidationException("User not found");
        }
        Optional<ParkingSpot> parkingSpot = parkingSpotService.getParkingSpot(reservationDTO.parkingSpotId());
        if (parkingSpot.isEmpty()) {
            throw new ModelValidationException("ParkingSpot not found");
        }
        if (reservationRepository.existsByUserAndParkingSpotAndStartTimeAndEndTime(user.get(),
                parkingSpot.get(), reservationDTO.startTime(), reservationDTO.endTime()))
        {
            throw new ModelAlreadyExistsException("Reservation already exists");
        }
        Reservation reservation = new Reservation();
        reservation.setUser(user.get());
        reservation.setParkingSpot(parkingSpot.get());
        reservation.setStartTime(reservationDTO.startTime());
        reservation.setEndTime(reservationDTO.endTime());
        reservation.setTotalCost(reservationDTO.totalCost());
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> update(Long id, Reservation updatedReservation) {
        // TODO: do this (update action)
        throw new NotImplementedException();
    }

    @Override
    public void delete(Long id) throws ModelNotFoundException {
        if (!reservationRepository.existsById(id)) {
            throw new ModelValidationException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }
}
