package by.astakhau.trainee.driverservice.data.repositories;

import by.astakhau.trainee.driverservice.data.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
