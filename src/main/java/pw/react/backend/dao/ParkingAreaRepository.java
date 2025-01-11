package pw.react.backend.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pw.react.backend.models.ParkingArea;
import pw.react.backend.models.User;

import java.util.Optional;

@Repository
public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long>, JpaSpecificationExecutor<ParkingArea> {
    Boolean existsByName(String name);
}
