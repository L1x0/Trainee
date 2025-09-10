package by.astakhau.trainee.tripservice.mappers;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class TripMapperImpl implements TripMapper {
    @Override
    public TripResponseDto toTripResponseDto(Trip trip) {
        return TripResponseDto.builder()
                .id(trip.getId())
                .destinationAddress(trip.getDestinationAddress())
                .driverName(trip.getDriverName())
                .originAddress(trip.getOriginAddress())
                .passengerName(trip.getPassengerName())
                .price(trip.getPrice())
                .status(trip.getStatus())
                .build();
    }

    @Override
    public Trip TripRequestDtoToTrip(TripRequestDto tripRequestDto) {
        return Trip.builder()
                .id(null)
                .destinationAddress(tripRequestDto.getDestinationAddress())
                .originAddress(tripRequestDto.getOriginAddress())
                .passengerName(tripRequestDto.getPassengerName())
                .createdAt(OffsetDateTime.now())
                .driverId(null)
                .price(null)
                .orderDateTime(OffsetDateTime.now())
                .driverName(null)
                .isDeleted(false)
                .passengerId(tripRequestDto.getPassengerId())
                .status(TripStatus.CREATED)
                .build();
    }
}
