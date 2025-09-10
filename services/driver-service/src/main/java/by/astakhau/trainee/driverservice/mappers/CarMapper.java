package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.entities.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

public interface CarMapper {
    CarResponseDto carToCarResponseDto(Car Car);
    Car fromRequestDto(CarRequestDto CarRequestDto, Driver  driver);
}
