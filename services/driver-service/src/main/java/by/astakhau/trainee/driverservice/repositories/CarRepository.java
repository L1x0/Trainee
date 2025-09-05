package by.astakhau.trainee.driverservice.repositories;

import by.astakhau.trainee.driverservice.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Query(value = "SELECT * FROM cars WHERE id = ?", nativeQuery = true)
    Optional<Car> findById(long id);
}
