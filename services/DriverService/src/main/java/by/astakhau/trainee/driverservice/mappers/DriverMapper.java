package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.entities.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface DriverMapper {
    DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    DriverRequestDto driverToDriverRequestDto(Driver Driver);
    DriverResponseDto driverToDriverResponseDto(Driver Driver);
    Driver fromRequestDto(DriverRequestDto DriverRequestDto);
    Driver fromResponseDto(DriverResponseDto DriverResponseDto);
    TripDto DriverToTripDto(Driver Driver);
    Driver fromTripDto(TripDto Driver);
}
