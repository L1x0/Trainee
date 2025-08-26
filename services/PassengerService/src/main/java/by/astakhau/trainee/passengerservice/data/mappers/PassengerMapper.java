package by.astakhau.trainee.passengerservice.data.mappers;

import by.astakhau.trainee.passengerservice.data.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.data.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.data.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    PassengerResponseDto passengerToPassengerResponseDto(Passenger passenger);
    Passenger fromRequestDto(PassengerRequestDto passengerRequestDto);
}
