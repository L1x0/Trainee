package by.astakhau.trainee.passengerservice.mappers;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;

public interface PassengerMapper {
    PassengerResponseDto passengerToPassengerResponseDto(Passenger passenger);
    Passenger fromRequestDto(PassengerRequestDto passengerRequestDto);
}
