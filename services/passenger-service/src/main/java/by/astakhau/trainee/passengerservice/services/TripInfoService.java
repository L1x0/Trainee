package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.TripInfo;
import by.astakhau.trainee.passengerservice.entities.TripStatus;
import by.astakhau.trainee.passengerservice.mappers.TripMapper;
import by.astakhau.trainee.passengerservice.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripInfoService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public void saveOrUpdateTripInfo(TripResponseDto tripResponseDto) {
        log.info("Saving trip response: {}", tripResponseDto);

        var trip = tripRepository.findById(tripResponseDto.getId());


        if (TripStatus.COMPLETED.equals(tripResponseDto.getStatus())) {
            if (trip.isEmpty()) {
                throw new ResourceNotFoundException("Trip not found");
            } else {

                trip.get().setStatus(tripResponseDto.getStatus());
                tripRepository.save(trip.get());
                log.info("Updated trip response: {}", tripResponseDto);
            }
        } else {
            if (trip.isPresent()) {
                throw new IllegalStateException("Trip already exists");
            } else {
                tripRepository.save(tripMapper.toTripInfo(tripResponseDto));
                log.info("Saved trip response: {}", tripResponseDto);
            }
        }
    }

    public TripResponseDto getTripInfo(String passengerName) {
        var trip = tripRepository.findActiveTripsByPassengerName(passengerName);

        if (trip.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Trip not found");
        } else {
            return tripMapper.toTripResponse(trip.get());
        }
    }
}
