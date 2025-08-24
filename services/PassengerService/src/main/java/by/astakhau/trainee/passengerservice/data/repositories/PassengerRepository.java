package by.astakhau.trainee.passengerservice.data.repositories;

import by.astakhau.trainee.passengerservice.data.entities.Passenger;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Passenger findById(long id);
    List<Passenger> findByName(String firstName);
    List<Passenger> findByIsDeleted(boolean isDeleted);
    void deleteById(long id);
    void delete(@NonNull Passenger passenger);
}
