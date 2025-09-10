package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.entities.Driver;

public interface DriverMapper {
    DriverResponseDto driverToDriverResponseDto(Driver Driver);
    Driver fromRequestDto(DriverRequestDto DriverRequestDto);
    TripDto DriverToTripDto(Driver Driver);
}
