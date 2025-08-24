package by.astakhau.trainee.driverservice.data.repositories;

import by.astakhau.trainee.driverservice.data.entities.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByName(String name);
    Driver findByEmail(String email);
    Driver findByPhoneNumber(String phoneNumber);

    Driver findByIsDeleted(boolean deleted);
}
