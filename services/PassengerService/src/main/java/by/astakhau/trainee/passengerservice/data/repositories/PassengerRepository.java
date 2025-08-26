package by.astakhau.trainee.passengerservice.data.repositories;

import by.astakhau.trainee.passengerservice.data.entities.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByNameAndPhoneNumber(String name, String phoneNumber);
    Page<Passenger> findByName(String name, Pageable pageable);
    Page<Passenger> findByIsDeleted(Boolean isDeleted,  Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE passengers SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE name = :name AND email = :email", nativeQuery = true)
    void softDeleteByNameAndEmail(@Param("name") String name, @Param("email") String email);

}
