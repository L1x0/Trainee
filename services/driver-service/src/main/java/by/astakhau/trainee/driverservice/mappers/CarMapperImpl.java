package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.entities.Driver;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class CarMapperImpl implements CarMapper {
    @Override
    public CarResponseDto carToCarResponseDto(Car car) {
        return CarResponseDto.builder()
                .color(car.getColor())
                .make(car.getMake())
                .plateNumber(car.getPlateNumber())
                .createdAt(car.getCreatedAt())
                .isDeleted(car.getIsDeleted())
                .build();
    }

    @Override
    public Car fromRequestDto(CarRequestDto carRequestDto, Driver driver) {
        return Car.builder()
                .id(null)
                .isDeleted(false)
                .createdAt(OffsetDateTime.now())
                .color(carRequestDto.getColor())
                .driver(driver)
                .version(0)
                .plateNumber(carRequestDto.getPlateNumber())
                .make(carRequestDto.getMake())
                .deletedAt(null)
                .build();

    }
}
