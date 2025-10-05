package by.astakhau.trainee.passengerservice.repositories;

import by.astakhau.trainee.passengerservice.entities.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByNameAndPhoneNumber(String name, String phoneNumber);
    Page<Passenger> findByName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM passengers WHERE id = ?", nativeQuery = true)
    Optional<Passenger> findById(Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE passengers SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE name = :name AND email = :email", nativeQuery = true)
    void softDeleteByNameAndEmail(@Param("name") String name, @Param("email") String email);

}
