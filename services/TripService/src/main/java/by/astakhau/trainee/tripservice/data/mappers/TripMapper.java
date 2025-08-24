package by.astakhau.trainee.tripservice.data.mappers;

import by.astakhau.trainee.tripservice.data.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.data.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.data.entities.Trip;
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
