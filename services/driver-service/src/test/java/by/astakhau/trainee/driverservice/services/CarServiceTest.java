package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.mappers.CarMapper;
import by.astakhau.trainee.driverservice.repositories.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @InjectMocks
    private CarService carService;
    @Mock
    private CarMapper carMapper;

    @Test
    void findAll_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());

        Car c1 = new Car();
        c1.setId(1L);
        Car c2 = new Car();
        c2.setId(2L);

        Page<Car> page = new PageImpl<>(List.of(c1, c2), pageable, 2);
        when(carRepository.findAll(pageable)).thenReturn(page);

        CarResponseDto dto1 = new CarResponseDto();
        CarResponseDto dto2 = new CarResponseDto();

        when(carMapper.carToCarResponseDto(c1)).thenReturn(dto1);
        when(carMapper.carToCarResponseDto(c2)).thenReturn(dto2);

        Page<CarResponseDto> result = carService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(carRepository).findAll(pageable);
        verify(carMapper).carToCarResponseDto(c1);
        verify(carMapper).carToCarResponseDto(c2);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    void findAll_emptyPage_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Car> empty = Page.empty(pageable);
        when(carRepository.findAll(pageable)).thenReturn(empty);

        Page<CarResponseDto> result = carService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository).findAll(pageable);
        verifyNoInteractions(carMapper);
    }

    @Test
    void findById_whenFound() {
        Car car = new Car();
        car.setId(10L);
        when(carRepository.findById(10L)).thenReturn(Optional.of(car));

        CarResponseDto dto = CarResponseDto.builder().plateNumber("113413").build();
        when(carMapper.carToCarResponseDto(car)).thenReturn(dto);

        Optional<CarResponseDto> result = carService.findById(10L);

        assertTrue(result.isPresent());
        verify(carRepository).findById(10L);
        verify(carMapper).carToCarResponseDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    void findById_whenNotFound() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<CarResponseDto> result = carService.findById(999L);

        assertFalse(result.isPresent());
        verify(carRepository).findById(999L);
        verifyNoInteractions(carMapper);
    }
}
