package by.astakhau.trainee.driverservice.repositories;

import by.astakhau.trainee.driverservice.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Page<Driver> findByName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM drivers JOIN cars on drivers.id = cars.driver_id where driver_id = 1;", nativeQuery = true)
    Optional<Driver> findById(Long id);

    @Modifying
    @Query(value = "UPDATE drivers SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE name = :name AND email = :email", nativeQuery = true)
    void softDeleteByNameAndEmail(@Param("name") String name, @Param("email") String email);

    @Modifying
    @Query(value = "UPDATE drivers SET is_busy = false WHERE id = :driver_id", nativeQuery = true)
    void ridDriver(@Param("driver_id") Long driverId);

    Optional<Driver> findByEmail(String email);

    Optional<Driver> findFirstByIsBusy(boolean isBusy);

    Driver save(Driver driver);
}
