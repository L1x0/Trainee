package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.grpc.DriverGrpcClient;
import by.astakhau.trainee.tripservice.kafka.CreatedTripsProducer;
import by.astakhau.trainee.tripservice.mappers.TripMapper;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {
    @InjectMocks
    private TripService tripService;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripMapper tripMapper;
    @Mock
    DriverGrpcClient driverGrpcClient;
    @Mock
    CreatedTripsProducer createdTripsProducer;

    @Test
    void createTrip() {
        GetDriverResponse driver = GetDriverResponse.newBuilder()
                .setDriverId(1L)
                .setName("Driver 1")
                .build();

        var trip = Trip.builder()
                .id(1L)
                .build();

        var tripRequestDto = new TripRequestDto();
        var tripResponseDto = TripResponseDto.builder()
                .id(1L)
                .passengerName("Passenger 1")
                .build();

        when(driverGrpcClient.getDriver()).thenReturn(driver);
        when(tripMapper.TripRequestDtoToTrip(tripRequestDto)).thenReturn(trip);
        when(tripMapper.toTripResponseDto(any(Trip.class))).thenReturn(tripResponseDto);

        assertEquals("Passenger 1", tripService.createTrip(tripRequestDto).getPassengerName());

        verify(tripRepository, times(1)).save(trip);
    }

    @Test
    void updateTrip() {
        var tripRequestDto = TripRequestDto.builder()
                .passengerName("Passenger 1")
                .originAddress(null)
                .destinationAddress("Destination 1")
                .build();

        var responseDto = TripResponseDto.builder()
                .id(1L)
                .passengerName("Passenger 1")
                .originAddress(null)
                .destinationAddress("Destination 1")
                .build();

        var trip = Trip.builder()
                .id(1L)
                .build();


        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripMapper.toTripResponseDto(any(Trip.class))).thenReturn(responseDto);
        when(tripRepository.save(trip)).thenReturn(trip);

        assertEquals(responseDto, tripService.update(1L, tripRequestDto));

        verify(tripRepository, times(1)).findById(1L);
        verify(tripRepository, times(1)).save(trip);
    }

    @Test
    void changeStatus() {
        var trip = Trip.builder()
                .id(1L)
                .driverId(1L)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripMapper.toTripResponseDto(trip)).thenReturn(TripResponseDto.builder().id(1L).build());

        tripService.changeStatus(1L, TripStatus.COMPLETED);

        verify(tripRepository, times(1)).findById(1L);
        verify(tripRepository, times(1)).save(trip);
        verify(createdTripsProducer, times(1)).send(any(TripResponseDto.class));
    }

    @Test
    void endTrip() {
        var trip = Trip.builder()
                .id(1L)
                .driverId(1L)
                .build();

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        tripService.endOfTrip(1L);

        verify(tripRepository, times(1)).findById(1L);
        verify(tripRepository, times(1)).save(trip);
        verify(driverGrpcClient, times(1)).ridDriver(1L);
    }

    @Test
    void findById_whenFound_returnsMappedDto() {
        Trip trip = new Trip();
        trip.setId(100L);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(trip));

        TripResponseDto dto = TripResponseDto.builder().id(100L).build();
        when(tripMapper.toTripResponseDto(trip)).thenReturn(dto);

        Optional<TripResponseDto> result = tripService.findById(100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
        verify(tripRepository).findById(100L);
        verify(tripMapper).toTripResponseDto(trip);
        verifyNoMoreInteractions(tripRepository, tripMapper);
    }

    @Test
    void findById_whenNotFound_returnsEmpty() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<TripResponseDto> result = tripService.findById(999L);

        assertFalse(result.isPresent());
        verify(tripRepository).findById(999L);
        verifyNoInteractions(tripMapper);
    }

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());

        Trip t1 = new Trip(); t1.setId(1L);
        Trip t2 = new Trip(); t2.setId(2L);
        Page<Trip> page = new PageImpl<>(List.of(t1, t2), pageable, 2);
        when(tripRepository.findAll(pageable)).thenReturn(page);

        TripResponseDto dto1 = TripResponseDto.builder().id(1L).build();
        TripResponseDto dto2 = TripResponseDto.builder().id(2L).build();
        when(tripMapper.toTripResponseDto(t1)).thenReturn(dto1);
        when(tripMapper.toTripResponseDto(t2)).thenReturn(dto2);

        Page<TripResponseDto> result = tripService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(tripRepository).findAll(pageable);
        verify(tripMapper).toTripResponseDto(t1);
        verify(tripMapper).toTripResponseDto(t2);
        verifyNoMoreInteractions(tripRepository, tripMapper);
    }

    @Test
    void findAll_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Trip> empty = Page.empty(pageable);
        when(tripRepository.findAll(pageable)).thenReturn(empty);

        Page<TripResponseDto> result = tripService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tripRepository).findAll(pageable);
        verifyNoInteractions(tripMapper);
    }

    @Test
    void findAllByStatus_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 3);
        Trip t = new Trip(); t.setId(55L);
        Page<Trip> page = new PageImpl<>(List.of(t), pageable, 1);

        when(tripRepository.findAllByStatus(pageable, TripStatus.CREATED)).thenReturn(page);

        TripResponseDto dto = TripResponseDto.builder().id(55L).build();
        when(tripMapper.toTripResponseDto(t)).thenReturn(dto);

        Page<TripResponseDto> result = tripService.findAllByStatus(pageable, TripStatus.CREATED);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(55L, result.getContent().get(0).getId());

        verify(tripRepository).findAllByStatus(pageable, TripStatus.CREATED);
        verify(tripMapper).toTripResponseDto(t);
        verifyNoMoreInteractions(tripRepository, tripMapper);
    }

    @Test
    void findAllByStatus_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(tripRepository.findAllByStatus(pageable, TripStatus.CANCELLED)).thenReturn(Page.empty(pageable));

        Page<TripResponseDto> result = tripService.findAllByStatus(pageable, TripStatus.CANCELLED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tripRepository).findAllByStatus(pageable, TripStatus.CANCELLED);
        verifyNoInteractions(tripMapper);
    }
}
