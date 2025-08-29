package by.astakhau.trainee.passengerservice.mappers;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    PassengerResponseDto passengerToPassengerResponseDto(Passenger passenger);
    Passenger fromRequestDto(PassengerRequestDto passengerRequestDto);

    TripRequestDto TripRequestDtoFromPassenger(Passenger passenger);
    Passenger fromPassengerRequestDto(PassengerRequestDto passengerRequestDto);
}
