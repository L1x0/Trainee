package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.kafka.CreatedTripsProducer;
import by.astakhau.trainee.tripservice.mappers.TripMapper;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final DriverGrpcClient driverGrpcClient;
    private final CreatedTripsProducer createdTripsProducer;

    public TripResponseDto createTrip(TripRequestDto passengerOrderDto) {
        log.info("Creating trip");

        var driver = driverGrpcClient.getDriver();

        log.info("driver that i had: {}", driver.toString());

        Trip createdTrip = tripMapper.TripRequestDtoToTrip(passengerOrderDto);

        createdTrip.setDriverName(driver.getName());
        createdTrip.setPrice((int) (Math.random() * 10));
        createdTrip.setDriverId(driver.getDriverId());

        tripRepository.save(createdTrip);

        return tripMapper.toTripResponseDto(createdTrip);
    }

    public Optional<TripResponseDto> findById(Long id) {
        return tripRepository.findById(id).map(tripMapper::toTripResponseDto);
    }

    public Page<TripResponseDto> findAll(Pageable pageable) {
        return tripRepository.findAll(pageable).map(tripMapper::toTripResponseDto);
    }

    public Page<TripResponseDto> findAllByStatus(Pageable pageable, TripStatus status) {
        return tripRepository.findAllByStatus(pageable, status).map(tripMapper::toTripResponseDto);
    }

    public TripResponseDto update(Long id, TripRequestDto tripRequestDto) {
        Optional<Trip> trip = tripRepository.findById(id);

        if (trip.isPresent()) {

            log.info("trip before update: {}", trip.get());

            trip.get().setPassengerName(tripRequestDto.getPassengerName());
            trip.get().setDestinationAddress(tripRequestDto.getDestinationAddress());
            trip.get().setOriginAddress(tripRequestDto.getOriginAddress());

            log.info("trip after update: {}", trip.get());

            return tripMapper.toTripResponseDto(tripRepository.save(trip.get()));
        } else {
            log.error("trip is not found");
            return null;
        }
    }

    public void delete(String driverName, String destinationAddress) {
        log.info("delete trip with DriverName: {}, DestinationAddress: {}", driverName, destinationAddress);

        tripRepository.softDelete(driverName, destinationAddress);
    }

    public TripResponseDto save(TripRequestDto tripRequestDto) {
        log.info("save trip: {}", tripRequestDto.toString());
        System.out.println("save trip");

        var trip = createTrip(tripRequestDto);
        log.info("save trip: {}", trip);

        return trip;
    }

    @Transactional("transactionManager")
    public void changeStatus(Long id, TripStatus status) {
        var trip = tripRepository.findById(id);

        if (trip.isEmpty()) {
            log.error("trip is not found for change status");
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        } else {
            var value = trip.get();
            log.info("change trip status from: {}, to: {}", value.getStatus(), status);

            value.setStatus(status);
            tripRepository.save(value);

            if (TripStatus.COMPLETED.equals(status)) {
                log.info("trip has been completed, invoke kafka producer");
                createdTripsProducer.send(tripMapper.toTripResponseDto(value));
            }

        }
    }


    @Transactional("transactionManager")
    public void endOfTrip(Long id) {
        log.info("end trip with id: {}", id);

        tripRepository.findById(id)
                .ifPresent(value -> {
                    log.info("Trying to end trip: {}", value);

                    this.changeStatus(id, TripStatus.COMPLETED);

                    driverGrpcClient.ridDriver(value.getDriverId());
                    tripRepository.save(value);
                });
    }
}
