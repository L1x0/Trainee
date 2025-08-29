package by.astakhau.trainee.tripservice.mappers;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface TripMapper {
    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    public TripRequestDto toTripRequestDto(Trip trip);
    public TripResponseDto toTripResponseDto(Trip trip);
    public Trip TripRequestDtoToTrip(TripRequestDto tripRequestDto);
    public Trip TripResponseDtoToTrip(TripResponseDto tripResponseDto);
}
