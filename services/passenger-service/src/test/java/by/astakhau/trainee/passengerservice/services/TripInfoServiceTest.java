package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.TripInfo;
import by.astakhau.trainee.passengerservice.entities.TripStatus;
import by.astakhau.trainee.passengerservice.mappers.TripMapper;
import by.astakhau.trainee.passengerservice.repositories.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TripInfoServiceTest {
    @InjectMocks
    private TripInfoService tripInfoService;
    @Mock
    private TripRepository tripRepository;
    @Mock
    TripMapper tripMapper;

    @Test
    void saveOrUpdateTrip() {
        var trip = TripResponseDto.builder()
                .id(1L)
                .status(TripStatus.ACCEPTED)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        tripInfoService.saveOrUpdateTripInfo(trip);
        verify(tripRepository).save(tripMapper.toTripInfo(trip));

        trip.setStatus(TripStatus.COMPLETED);
        trip.setId(2L);

        var tripInfo = TripInfo.builder()
                .id(2L)
                .status(TripStatus.ACCEPTED)
                .build();

        when(tripRepository.findById(2L)).thenReturn(Optional.of(tripInfo));
        when(tripMapper.toTripResponse(any(TripInfo.class))).thenReturn(new TripResponseDto());

        tripInfoService.saveOrUpdateTripInfo(trip);

        verify(tripRepository).save(tripInfo);
    }

    @Test
    void getTripInfo() {
        var trip = TripInfo.builder()
                .id(1L)
                .status(TripStatus.ACCEPTED)
                .build();

        when(tripRepository.findActiveTripsByPassengerName(any(String.class))).thenReturn(Optional.of(trip));
        when(tripMapper.toTripResponse(any(TripInfo.class))).thenReturn(new TripResponseDto());

        assertEquals(Optional.of(new TripResponseDto()), tripInfoService.getTripInfo("passengerName"));
        verify(tripRepository).findActiveTripsByPassengerName(any(String.class));
        verify(tripMapper).toTripResponse(any(TripInfo.class));
    }
}
