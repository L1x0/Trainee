package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;
import by.astakhau.trainee.passengerservice.kafka.TripsOrderProducer;
import by.astakhau.trainee.passengerservice.mappers.PassengerMapper;
import by.astakhau.trainee.passengerservice.repositories.PassengerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    PassengerMapper passengerMapper;
    @Mock
    TripsOrderProducer tripsOrderProducer;
    @Mock
    TripInfoService tripInfoService;
    @InjectMocks
    PassengerService passengerService;
    @Captor
    ArgumentCaptor<Passenger> passengerCaptor;
    @Captor
    ArgumentCaptor<TripRequestDto> tripRequestCaptor;

    @Test
    void savePassenger() {
        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .email("email")
                .name("name")
                .phoneNumber("phoneNumber")
                .build();

        var mapped = Passenger.builder()
                .id(null)
                .email("email")
                .phoneNumber("phoneNumber")
                .name("name")
                .deletedAt(null)
                .isDeleted(false)
                .version(0)
                .build();

        when(passengerMapper.fromRequestDto(passengerRequestDto)).thenReturn(mapped);

        Passenger saved = Passenger
                .builder()
                .id(1L)
                .email("email")
                .phoneNumber("phoneNumber")
                .name("name")
                .deletedAt(null)
                .isDeleted(false)
                .version(0)
                .build();

        when(passengerRepository.save(any(Passenger.class))).thenReturn(saved);

        PassengerResponseDto responseDto = PassengerResponseDto.builder()
                .name("name")
                .phoneNumber("phoneNumber")
                .email("email")
                .id(1L)
                .build();

        when(passengerMapper.passengerToPassengerResponseDto(saved)).thenReturn(responseDto);

        PassengerResponseDto result = passengerService.savePassenger(passengerRequestDto);

        assertSame(responseDto, result, "Должен вернуться DTO полученный от mapper");
        verify(passengerRepository).save(passengerCaptor.capture());

        Passenger passedToSave = passengerCaptor.getValue();
        assertNotNull(passedToSave, "Объект для сохранения не null");
        assertNotEquals(Boolean.TRUE, passedToSave.getIsDeleted(), "isDeleted должен быть false");
        assertNull(passedToSave.getDeletedAt(), "deletedAt должен быть null");

        verify(passengerMapper).passengerToPassengerResponseDto(saved);
    }

    @Test
    void findPassengerById() {
        passengerService.findById(1L);
        verify(passengerRepository).findById(1L);
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());

        Passenger p1 = new Passenger();
        p1.setId(1L);
        p1.setName("Ivan");

        Passenger p2 = new Passenger();
        p2.setId(2L);
        p2.setName("Petr");

        Page<Passenger> page = new PageImpl<>(List.of(p1, p2), pageable, 2);

        when(passengerRepository.findAll(pageable)).thenReturn(page);

        PassengerResponseDto dto1 = new PassengerResponseDto();
        dto1.setId(1L);
        PassengerResponseDto dto2 = new PassengerResponseDto();
        dto2.setId(2L);

        when(passengerMapper.passengerToPassengerResponseDto(p1)).thenReturn(dto1);
        when(passengerMapper.passengerToPassengerResponseDto(p2)).thenReturn(dto2);

        Page<PassengerResponseDto> result = passengerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements(), "Ожидаем 2 элемента в странице");
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(passengerRepository).findAll(pageable);
        verify(passengerMapper).passengerToPassengerResponseDto(p1);
        verify(passengerMapper).passengerToPassengerResponseDto(p2);
        verifyNoMoreInteractions(passengerRepository, passengerMapper);
    }

    @Test
    void updatePassenger() {
        when(passengerRepository.findByNameAndPhoneNumber("name", "phoneNumber"))
                .thenReturn(Optional.of(Passenger.builder().id(1L).build()));

        PassengerRequestDto dto = PassengerRequestDto.builder()
                .phoneNumber("newPhoneNumber")
                .email("newEmail")
                .build();

        passengerService.update("name", "phoneNumber", dto);

        verify(passengerRepository).findByNameAndPhoneNumber("name", "phoneNumber");
        assertEquals("newPhoneNumber", dto.getPhoneNumber(), "Телефон не поменялся");
        assertEquals("newEmail", dto.getEmail(), "почта не поменялась");

    }

    @Test
    void createTripOrder() {
        var tripRequestDto = TripRequestDto.builder()
                .passengerId(1L)
                .passengerName("name")
                .passengerPhoneNumber("phoneNumber")
                .destinationAddress("address_d")
                .originAddress("address_o")
                .build();

        when(passengerRepository.findByNameAndPhoneNumber(tripRequestDto.getPassengerName(), tripRequestDto.getPassengerPhoneNumber()))
                .thenReturn(Optional.of(Passenger.builder()
                        .id(1L)
                        .name("name")
                        .phoneNumber("phoneNumber")
                        .email("email")
                        .version(0)
                        .isDeleted(false)
                        .deletedAt(null)
                        .build()));

        passengerService.createTripOrder(tripRequestDto);
        verify(tripsOrderProducer).sendTripRequest(tripRequestDto);
    }

    @Test
    void getTripInfo() {
        TripResponseDto dto = TripResponseDto.builder()
                .passengerName("name")
                .build();

        when(tripInfoService.getTripInfo("name")).thenReturn(Optional.of(dto));
        passengerService.getTripInfo("name");
        verify(tripInfoService).getTripInfo("name");
    }

    @Test
    void deleteByEmail() {
        passengerService.deleteWithEmail("name", "email");
        verify(passengerRepository).softDeleteByNameAndEmail("name", "email");
    }
}
