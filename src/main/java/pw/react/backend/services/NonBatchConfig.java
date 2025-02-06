package pw.react.backend.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import pw.react.backend.dao.*;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.Reservation;

@Profile("!batch")
public class NonBatchConfig {

    @Bean
    @Qualifier("userService")
    public UserService userService(UserRepository userRepository) {
        return new UserMainService(userRepository);
    }

    @Bean
    @Qualifier("parkingAreaService")
    public ParkingAreaService parkingAreaService(ParkingAreaRepository parkingAreaRepository, ParkingSpotRepository parkingSpotRepository, ReservationRepository reservationRepository) {
        return new ParkingAreaMainService(parkingAreaRepository, parkingSpotRepository, reservationRepository);
    }

    @Bean
    @Qualifier("parkingSpotService")
    public ParkingSpotService parkingSpotService(ParkingSpotRepository parkingSpotRepository, ParkingAreaRepository parkingAreaRepository, ReservationRepository reservationRepository) {
        return new ParkingSpotMainService(parkingSpotRepository, parkingAreaRepository, reservationRepository);
    }

    @Bean
    @Qualifier("reservationService")
    public ReservationService reservationService(ReservationRepository reservationRepository, UserService userService, ParkingSpotService parkingSpotService, ParkingAreaService parkingAreaService) {
        return new ReservationMainService(reservationRepository, userService, parkingSpotService);
    }
}
