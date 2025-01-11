package pw.react.backend.specifications;

import org.springframework.data.jpa.domain.Specification;
import pw.react.backend.models.ParkingArea;

public class ParkingAreaSpecification {
    public static Specification<ParkingArea> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ParkingArea> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + address.toLowerCase() + "%");
        };
    }

    public static Specification<ParkingArea> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + city.toLowerCase() + "%");
        };
    }
}
