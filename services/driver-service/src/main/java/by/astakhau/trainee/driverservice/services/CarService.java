package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.mappers.CarMapper;
import by.astakhau.trainee.driverservice.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public Page<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::carToCarResponseDto);
    }

    public CarResponseDto findById(Long id) {
        return carMapper.carToCarResponseDto(carRepository.findById(id).orElse(null));
    }
}
