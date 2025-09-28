package by.astakhau.trainee.passengerservice.mappers;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.TripInfo;

public interface TripMapper {
    TripResponseDto toTripResponse(TripInfo trip);
    TripInfo toTripInfo(TripResponseDto trip);
}
