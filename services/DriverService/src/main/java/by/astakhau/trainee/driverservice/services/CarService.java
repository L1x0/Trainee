package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.data.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.data.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.data.entities.Car;
import by.astakhau.trainee.driverservice.data.mappers.CarMapper;
import by.astakhau.trainee.driverservice.data.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Transactional
    public void save(CarRequestDto carRequestDto) {
        Car car = carMapper.fromRequestDto(carRequestDto);

        car.setCreatedAt(OffsetDateTime.now());
        car.setIsDeleted(false);
        car.setDeletedAt(null);

        carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponseDto> findAll() {
        var result = carRepository.findAll();

        List<CarResponseDto> cars = new ArrayList<>();
        result.forEach(car -> {
            cars.add(carMapper.carToCarResponseDto(car));
        });

        return cars;
    }

    @Transactional(readOnly = true)
    public CarResponseDto findById(Long id) {
        Car car = carRepository.findById(id).orElse(null);
        return carMapper.carToCarResponseDto(car);
    }

    @Transactional
    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }
}
