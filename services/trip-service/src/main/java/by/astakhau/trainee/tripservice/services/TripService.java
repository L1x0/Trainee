package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.tripservice.dtos.PassengerOrderDto;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public TripResponseDto createTrip(PassengerOrderDto passengerOrderDto) {
        /*
        тут будет инициироваться подбор водителя
         и объединение данных для формирования поездки
        */

        return null;
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

            trip.get().setPassengerName(tripRequestDto.getPassengerName());
            trip.get().setDestinationAddress(tripRequestDto.getDestinationAddress());
            trip.get().setOriginAddress(tripRequestDto.getOriginAddress());

            log.info("trip after update: {}", trip.get());

            return tripMapper.toTripResponseDto(tripRepository.save(trip.get()));
        }
        else  {
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
    public void save(TripRequestDto trip) {
        log.info("save trip: {}", trip);

        tripRepository.save(tripMapper.TripRequestDtoToTrip(trip));
    }

    @Transactional
    public void changeStatus(String passengerName, String driverName, TripStatus status) {// мб лучше сделать по функции на перевод в каждый статус
        tripRepository.findByDriverNameAndPassengerName(driverName, passengerName)
                .ifPresent(value -> {
                    log.info("change trip status from: {}, to: {}", value.getStatus(), status);

                    value.setStatus(status);
                    tripRepository.save(value);
                });
    }
}
