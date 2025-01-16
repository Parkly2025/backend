package pw.react.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pw.react.backend.dto.CreateReservationDTO;
import pw.react.backend.models.Reservation;

import java.util.Optional;

@Service
public interface ReservationService {
    Page<Reservation> findAll(int page, int size, String sortDirection);
    Optional<Reservation> findById(Long id);
    Reservation create(CreateReservationDTO reservationDTO);
    Reservation update(Long id, CreateReservationDTO reservationDTO);
    void delete(Long id);
    Page<Reservation> findByUserId(int page, int size, String sortDirection, Long userId);
}
