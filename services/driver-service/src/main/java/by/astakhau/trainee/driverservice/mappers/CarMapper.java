package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.entities.Driver;

public interface CarMapper {
    CarResponseDto carToCarResponseDto(Car Car);
    Car fromRequestDto(CarRequestDto CarRequestDto, Driver  driver);
}
