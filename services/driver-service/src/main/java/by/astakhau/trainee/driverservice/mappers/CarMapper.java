package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    CarRequestDto carToCarRequestDto(Car car);
    CarResponseDto carToCarResponseDto(Car Car);
    Car fromRequestDto(CarRequestDto CarRequestDto);
    Car fromResponseDto(CarResponseDto CarResponseDto);
}
