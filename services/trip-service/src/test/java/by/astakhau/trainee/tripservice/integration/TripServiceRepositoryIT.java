package by.astakhau.trainee.tripservice.integration;

import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TripServiceRepositoryIT extends AbstractIntegrationTest {
    @Autowired
    private TripRepository tripRepository;

    @Transactional
    @Rollback
    @Test
    public void save_and_findTest() {
        var trip = Trip.builder()
                .id(null)
                .createdAt(OffsetDateTime.now())
                .price(7)
                .destinationAddress("Destination address")
                .driverId(1L)
                .driverName("driver")
                .orderDateTime(OffsetDateTime.now())
                .originAddress("Origin address")
                .passengerId(1L)
                .passengerName("passenger")
                .isDeleted(false)
                .status(TripStatus.COMPLETED)
                .build();

        tripRepository.save(trip);

        var found = tripRepository.findByDriverNameAndPassengerName("driver", "passenger");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getDriverName()).isEqualTo("driver");
    }

    @Transactional
    @Rollback
    @Test
    public void softDeleteTest() {
        var trip = Trip.builder()
                .id(null)
                .createdAt(OffsetDateTime.now())
                .price(7)
                .destinationAddress("Destination address")
                .driverId(1L)
                .driverName("driver")
                .orderDateTime(OffsetDateTime.now())
                .originAddress("Origin address")
                .passengerId(1L)
                .passengerName("passenger")
                .isDeleted(false)
                .status(TripStatus.COMPLETED)
                .build();

        tripRepository.save(trip);

        var before = tripRepository.findByDriverNameAndPassengerName("driver", "passenger");

        tripRepository.softDelete("driver", "Destination address");

        var after = tripRepository.findById(before.get().getId());

        assertThat(after).isPresent();
        assertThat(after.get().getId()).isNotNull();
        assertThat(after.get().getIsDeleted()).isTrue();
    }
}
