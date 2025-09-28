package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.client.TripClient;
import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;
import by.astakhau.trainee.passengerservice.kafka.TripsOrderProducer;
import by.astakhau.trainee.passengerservice.mappers.PassengerMapper;
import by.astakhau.trainee.passengerservice.repositories.PassengerRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerService {

    final private PassengerRepository passengerRepository;
    final private PassengerMapper passengerMapper;
    //final private TripClient tripClient;
    final private TripsOrderProducer tripsOrderProducer;
    private final TripInfoService tripInfoService;

    @Transactional("transactionManager")
    public PassengerResponseDto savePassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerMapper.fromRequestDto(passengerRequestDto);

        passenger.setDeletedAt(null);
        passenger.setIsDeleted(false);

        var savedPassenger = passengerRepository.save(passenger);

        log.info("Passenger saved with ID: {}, phone number: {}, email: {}",
                passenger.getId(), passenger.getPhoneNumber(), passenger.getEmail());

        return passengerMapper.passengerToPassengerResponseDto(savedPassenger);
    }

    public Optional<PassengerResponseDto> findById(Long id) {
        return passengerRepository.findById(id).map(passengerMapper::passengerToPassengerResponseDto);
    }

    public Page<PassengerResponseDto> findAll(Pageable pageable) {
        var results = passengerRepository.findAll(pageable);

        return results.map(passengerMapper::passengerToPassengerResponseDto);
    }

    public Page<PassengerResponseDto> findAllByName(String name, Pageable pageable) {
        var results = passengerRepository.findByName(name, pageable);

        log.info("Passengers found with name: {}, pageable: {}", name, pageable);

        return results.map(passengerMapper::passengerToPassengerResponseDto);
    }

    @Transactional("transactionManager")
    public PassengerResponseDto update(String name, String phoneNumber, PassengerRequestDto passengerRequestDto) {
        var passenger = passengerRepository.findByNameAndPhoneNumber(name, phoneNumber);

        if (passenger.isPresent()) {
            log.info("Updating passenger");

            passenger.get().setEmail(passengerRequestDto.getEmail());
            passenger.get().setPhoneNumber(passengerRequestDto.getPhoneNumber());
            passengerRepository.save(passenger.get());

            log.info("Passenger updated");
            return passengerMapper.passengerToPassengerResponseDto(passenger.get());
        }

        log.error("No passenger found with name: {}, phoneNumber: {}", name, phoneNumber);
        throw new IllegalStateException("No passenger found with name: " + name);
    }


    @Transactional("transactionManager")
    public void deleteWithEmail(String name, String email) {
        passengerRepository.softDeleteByNameAndEmail(name, email);

        log.info("Passengers deleted with name: {}, email: {}", name, email);
    }

    @Transactional("transactionManager")
    @CircuitBreaker(name = "tripService", fallbackMethod = "createTripFallback")
    public void createTripOrder(TripRequestDto tripRequestDto) {
        var owner = getOrderOwner(tripRequestDto);

        if (owner.isEmpty())
            throw new IllegalArgumentException("There isn't people with same info");
        else {
            tripRequestDto.setId(owner.get().getId());

            log.info("Creating trip order for tripRequest: {}", tripRequestDto);

            tripsOrderProducer.sendTripRequest(tripRequestDto);

            /*var trip = tripClient.createTrip(tripRequestDto);
            log.info("order is created, trip: {}", trip);*/
        }
    }

    public void createTripFallback(TripRequestDto tripRequestDto, Throwable ex) {
        log.error("tripService fallback for createTrip, ex={}", ex.toString());

        if (ex instanceof HttpClientErrorException.BadRequest) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } else {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Trip service unavailable", ex);
        }
    }

    public TripResponseDto getTripInfo(String passengerName) {

        return tripInfoService.getTripInfo(passengerName);
    }

    private Optional<Passenger> getOrderOwner(TripRequestDto tripRequestDto) {
        log.info("Getting owner for trip request: {}", tripRequestDto.toString());

        return passengerRepository.findByNameAndPhoneNumber(
                tripRequestDto.getPassengerName(),
                tripRequestDto.getPassengerPhoneNumber());
    }
}
