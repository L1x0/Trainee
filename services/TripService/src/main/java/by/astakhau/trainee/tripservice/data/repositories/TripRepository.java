package by.astakhau.trainee.tripservice.data.repositories;

import by.astakhau.trainee.tripservice.data.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByOriginAddressAndDestinationAddressAndPassengerNameAndIsDeleted(
            String originAddress, String destinationAddress, String passengerName, Boolean deleted);

    default Optional<Trip> findTripByRequestInfo(String origin, String dest, String passenger) {
        return findByOriginAddressAndDestinationAddressAndPassengerNameAndIsDeleted(origin, dest, passenger, false);
    }
}
