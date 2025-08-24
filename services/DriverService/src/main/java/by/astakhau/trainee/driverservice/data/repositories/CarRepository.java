package by.astakhau.trainee.driverservice.data.repositories;

import by.astakhau.trainee.driverservice.data.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> getAll();
}
