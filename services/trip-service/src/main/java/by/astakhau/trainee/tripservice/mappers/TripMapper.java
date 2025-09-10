package by.astakhau.trainee.tripservice.mappers;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;


public interface TripMapper {
    TripResponseDto toTripResponseDto(Trip trip);

    Trip TripRequestDtoToTrip(TripRequestDto tripRequestDto);
}
