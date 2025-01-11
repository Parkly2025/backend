package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pw.react.backend.models.ParkingSpot;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByParkingSpot(ParkingSpot parkingSpot);
    boolean existsByUserAndParkingSpotAndStartTimeAndEndTime
            (User user, ParkingSpot parkingSpot, LocalDateTime startTime, LocalDateTime endTime);
}
