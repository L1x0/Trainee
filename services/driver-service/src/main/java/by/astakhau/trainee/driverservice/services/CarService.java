package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.mappers.CarMapper;
import by.astakhau.trainee.driverservice.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public Page<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::carToCarResponseDto);
    }

    public Optional<CarResponseDto> findById(Long id) {
        return carRepository.findById(id).map(carMapper::carToCarResponseDto);
    }
}
