package by.astakhau.trainee.driverservice.data.mappers;

import by.astakhau.trainee.driverservice.data.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.data.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.data.entities.Driver;
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
}
