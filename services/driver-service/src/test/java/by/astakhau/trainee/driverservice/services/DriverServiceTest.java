package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.entities.Driver;
import by.astakhau.trainee.driverservice.mappers.DriverMapper;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
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
public class DriverServiceTest {
    @InjectMocks
    private DriverService driverService;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverMapper driverMapper;

    @Test
    void save() {
        var requestDto = DriverRequestDto.builder()
                .email("email")
                .name("name")
                .car(new CarRequestDto())
                .phoneNumber("phoneNumber")
                .build();

        var driver =  Driver.builder()
                .email("email")
                .name("name")
                .id(1L)
                .phoneNumber("phoneNumber")
                .car(new Car())
                .createdAt(null)
                .deletedAt(null)
                .isBusy(false)
                .isDeleted(false)
                .version(0)
                .build();

        var response = DriverResponseDto.builder()
                .email("email")
                .name("name")
                .phoneNumber("phoneNumber")
                .car(new CarResponseDto())
                .isBusy(false)
                .isDeleted(false)
                .build();

        when(driverMapper.fromRequestDto(requestDto)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(response);

        assertEquals(response, driverService.save(requestDto));

        verify(driverRepository, times(1)).save(driver);
        verify(driverMapper, times(1)).driverToDriverResponseDto(driver);
    }

    @Test
    void update() {
        var requestDto = DriverRequestDto.builder()
                .email("email")
                .name("name")
                .car(new CarRequestDto())
                .phoneNumber("phoneNumberNew")
                .build();

        var driver =  Driver.builder()
                .email("email")
                .name("name")
                .id(1L)
                .phoneNumber("phoneNumber")
                .car(new Car())
                .createdAt(null)
                .deletedAt(null)
                .isBusy(false)
                .isDeleted(false)
                .version(0)
                .build();

        var response = DriverResponseDto.builder()
                .email("email")
                .name("name")
                .phoneNumber("phoneNumberNew")
                .car(new CarResponseDto())
                .isBusy(false)
                .isDeleted(false)
                .build();

        when(driverRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(response);

        assertEquals(response, driverService.update(requestDto));

        verify(driverRepository, times(1)).findByEmail(requestDto.getEmail());
        verify(driverMapper, times(1)).driverToDriverResponseDto(driver);
    }

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());

        Driver d1 = new Driver(); d1.setId(1L);
        Driver d2 = new Driver(); d2.setId(2L);

        Page<Driver> page = new PageImpl<>(List.of(d1, d2), pageable, 2);
        when(driverRepository.findAll(pageable)).thenReturn(page);

        DriverResponseDto dto1 = new DriverResponseDto(); dto1.setId(1L);
        DriverResponseDto dto2 = new DriverResponseDto(); dto2.setId(2L);
        when(driverMapper.driverToDriverResponseDto(d1)).thenReturn(dto1);
        when(driverMapper.driverToDriverResponseDto(d2)).thenReturn(dto2);

        Page<DriverResponseDto> result = driverService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(driverRepository).findAll(pageable);
        verify(driverMapper).driverToDriverResponseDto(d1);
        verify(driverMapper).driverToDriverResponseDto(d2);
        verifyNoMoreInteractions(driverRepository, driverMapper);
    }

    @Test
    void findAll_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Driver> empty = Page.empty(pageable);
        when(driverRepository.findAll(pageable)).thenReturn(empty);

        Page<DriverResponseDto> result = driverService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(driverRepository).findAll(pageable);
        verifyNoInteractions(driverMapper);
    }

    @Test
    void findById_whenFound_returnsMappedDto() {
        Driver driver = new Driver(); driver.setId(10L);
        when(driverRepository.findById(10L)).thenReturn(Optional.of(driver));

        DriverResponseDto dto = new DriverResponseDto(); dto.setId(10L);
        when(driverMapper.driverToDriverResponseDto(driver)).thenReturn(dto);

        Optional<DriverResponseDto> result = driverService.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        verify(driverRepository).findById(10L);
        verify(driverMapper).driverToDriverResponseDto(driver);
        verifyNoMoreInteractions(driverRepository, driverMapper);
    }

    @Test
    void findById_whenNotFound_returnsEmpty() {
        when(driverRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<DriverResponseDto> result = driverService.findById(999L);

        assertFalse(result.isPresent());
        verify(driverRepository).findById(999L);
        verifyNoInteractions(driverMapper);
    }

    @Test
    void deleteByNameAndEmail_callsRepository() {
        String name = "Ivan";
        String email = "ivan@mail";

        driverService.deleteByNameAndEmail(name, email);

        verify(driverRepository).softDeleteByNameAndEmail(name, email);
        verifyNoMoreInteractions(driverRepository);
        verifyNoInteractions(driverMapper);
    }

    @Test
    void deleteByNameAndEmail_whenRepositoryThrows_exceptionPropagates() {
        String name = "Ivan";
        String email = "ivan@mail";
        doThrow(new RuntimeException("DB down")).when(driverRepository).softDeleteByNameAndEmail(name, email);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> driverService.deleteByNameAndEmail(name, email));
        assertEquals("DB down", ex.getMessage());

        verify(driverRepository).softDeleteByNameAndEmail(name, email);
    }
}
