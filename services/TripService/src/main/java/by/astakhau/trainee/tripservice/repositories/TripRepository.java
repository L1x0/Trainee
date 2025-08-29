package by.astakhau.trainee.tripservice.repositories;

import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Page<Trip> findAllByStatus(Pageable pageable, TripStatus status);

    Optional<Trip> findByDriverNameAndPassengerName(@Param("driver_name") String driverName,
                                                    @Param("passenger_name") String passengerName);


    @Query(value = "SELECT * FROM trips WHERE id = ?", nativeQuery = true)
    Optional<Trip> findById(Long id);


    @Modifying
    @Query(value = "UPDATE trips SET is_deleted = true" +
            " WHERE driver_name = :name AND destination_address = :destination_address", nativeQuery = true)
    void softDelete(@Param("name") String name, @Param("destination_address") String destination_address);


    Optional<Trip> findByOriginAddressAndDestinationAddressAndPassengerName(
            String originAddress, String destinationAddress, String passengerName);

    default Optional<Trip> findTripByRequestInfo(String origin, String dest, String passenger) {
        return findByOriginAddressAndDestinationAddressAndPassengerName(origin, dest, passenger);
    }
}
