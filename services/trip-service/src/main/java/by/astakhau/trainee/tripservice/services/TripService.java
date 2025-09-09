package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.mappers.TripMapper;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final DriverGrpcClient driverGrpcClient;

    public TripResponseDto createTrip(TripRequestDto passengerOrderDto) {
        log.info("Creating trip");
        var driver = driverGrpcClient.getDriver();
        log.info("driver that i had: {}", driver.toString());

        //вынести в маппер
        Trip createdTrip = new Trip();
        createdTrip.setDriverName(driver.getName());
        createdTrip.setStatus(TripStatus.CREATED);
        createdTrip.setPassengerId(passengerOrderDto.getId());
        createdTrip.setPassengerName(passengerOrderDto.getName());
        createdTrip.setDestinationAddress(passengerOrderDto.getDestinationAddress());
        createdTrip.setOriginAddress(passengerOrderDto.getOriginAddress());
        createdTrip.setPrice((int) Math.random());
        createdTrip.setDriverId(driver.getDriverId());
        createdTrip.setIsDeleted(false);
        createdTrip.setOrderDateTime(OffsetDateTime.now());

        tripRepository.save(createdTrip);

        return tripMapper.toTripResponseDto(createdTrip);
    }

    public TripResponseDto findById(Long id) {
        return tripMapper.toTripResponseDto(tripRepository.findById(id).orElse(null));
    }

    public Page<TripResponseDto> findAll(Pageable pageable) {
        return tripRepository.findAll(pageable).map(tripMapper::toTripResponseDto);
    }

    public Page<TripResponseDto> findAllByStatus(Pageable pageable, TripStatus status) {
        return tripRepository.findAllByStatus(pageable, status).map(tripMapper::toTripResponseDto);
    }

    public TripResponseDto update(String passengerName, String driverName, TripRequestDto tripRequestDto) {
        Optional<Trip> trip = tripRepository.findByDriverNameAndPassengerName(driverName, passengerName);

        if (trip.isPresent()) {

            log.info("trip before update: {}", trip.get());

            trip.get().setPassengerName(tripRequestDto.getName());
            trip.get().setDestinationAddress(tripRequestDto.getDestinationAddress());
            trip.get().setOriginAddress(tripRequestDto.getOriginAddress());

            log.info("trip after update: {}", trip.get());

            return tripMapper.toTripResponseDto(tripRepository.save(trip.get()));
        } else {
            log.error("trip is not found");
            return null;
        }
    }

    @Transactional
    public void delete(String driverName, String destinationAddress) {
        log.info("delete trip with DriverName: {}, DestinationAddress: {}", driverName, destinationAddress);

        tripRepository.softDelete(driverName, destinationAddress);
    }

    @Transactional
    public void save(TripRequestDto tripRequestDto) {
        log.info("save trip: {}", tripRequestDto.toString());
        System.out.println("save trip");

        var trip = createTrip(tripRequestDto);
        log.info("save trip: {}", trip);
    }

    //Разобраться со статусами!!!!!!!!!! если будет время накинуть события
    @Transactional
    public void changeStatus(String passengerName, String driverName, TripStatus status) {// мб лучше сделать по функции на перевод в каждый статус
        tripRepository.findByDriverNameAndPassengerName(driverName, passengerName)
                .ifPresent(value -> {
                    log.info("change trip status from: {}, to: {}", value.getStatus(), status);

                    value.setStatus(status);
                    tripRepository.save(value);
                });
    }

    @Transactional
    public void endOfTrip(String passengerName, String driverName) {
        log.info("end trip with DriverName: {}, PassengerName: {}", driverName, passengerName);

        tripRepository.findByDriverNameAndPassengerName(driverName, passengerName)
                .ifPresent(value -> {
                    log.info("Trying to end trip: {}", value);

                    value.setStatus(TripStatus.COMPLETED);

                    driverGrpcClient.ridDriver(value.getDriverId());
                    tripRepository.save(value);
                });
    }
}
