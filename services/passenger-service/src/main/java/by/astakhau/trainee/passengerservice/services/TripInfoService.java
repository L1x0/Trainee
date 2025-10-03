package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.TripInfo;
import by.astakhau.trainee.passengerservice.entities.TripStatus;
import by.astakhau.trainee.passengerservice.mappers.TripMapper;
import by.astakhau.trainee.passengerservice.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripInfoService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public void saveOrUpdateTripInfo(TripResponseDto tripResponseDto) {
        log.info("Saving trip response: {}", tripResponseDto);

        var trip = tripRepository.findById(tripResponseDto.getId());

        boolean isPresent = trip.isPresent();

        if (isPresent) {
            if (tripMapper.toTripResponse(trip.get()).equals(tripResponseDto)) {
                return;
            }
        }

        if (TripStatus.COMPLETED.equals(tripResponseDto.getStatus())) {
            updateTripStatus(tripResponseDto, trip,  isPresent);
        } else {
            saveNewTripInfo(tripResponseDto, isPresent);
        }
    }

    private void updateTripStatus(TripResponseDto tripResponseDto, Optional<TripInfo> trip, boolean isPresent) {
        if (!isPresent) {
            throw new ResourceNotFoundException("Trip not found");
        } else {

            if (TripStatus.COMPLETED.equals(trip.get().getStatus()))
                return;

            trip.get().setStatus(TripStatus.COMPLETED);
            tripRepository.save(trip.get());
            log.info("Updated trip response: {}", tripResponseDto);
        }
    }

    private void saveNewTripInfo(TripResponseDto tripResponseDto, boolean isPresent) {
        if (isPresent) {
            throw new IllegalStateException("Trip already exists");
        } else {
            tripRepository.save(tripMapper.toTripInfo(tripResponseDto));
            log.info("Saved trip response: {}", tripResponseDto);
        }
    }

    public Optional<TripResponseDto> getTripInfo(String passengerName) {
        var trip = tripRepository.findActiveTripsByPassengerName(passengerName);
        return trip.map(tripMapper::toTripResponse);
    }
}
