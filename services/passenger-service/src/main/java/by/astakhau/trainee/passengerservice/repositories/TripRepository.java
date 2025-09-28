package by.astakhau.trainee.passengerservice.repositories;

import by.astakhau.trainee.passengerservice.entities.TripInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<TripInfo, Long> {
    @Query(value = "SELECT * FROM trips WHERE passenger_name = :name AND status != 'COMPLETED'", nativeQuery = true)
    Optional<TripInfo> findActiveTripsByPassengerName(String name);
}
