package pw.react.backend.specifications;

import org.springframework.data.jpa.domain.Specification;
import pw.react.backend.models.Reservation;

public class ReservationSpecification {
    public static Specification<Reservation> hasParkingSpotCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("parkingSpot").get("city")),
                    "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<Reservation> hasParkingSpotAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("parkingSpot").get("address")),
                    "%" + address.toLowerCase() + "%");
        };
    }
}
