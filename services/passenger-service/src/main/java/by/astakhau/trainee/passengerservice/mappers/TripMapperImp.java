package by.astakhau.trainee.passengerservice.mappers;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.entities.TripInfo;
import org.springframework.stereotype.Component;

@Component
public class TripMapperImp implements TripMapper {

    @Override
    public TripResponseDto toTripResponse(TripInfo trip) {
        return TripResponseDto.builder()
                .id(trip.getId())
                .status(trip.getStatus())
                .destinationAddress(trip.getDestinationAddress())
                .originAddress(trip.getOriginAddress())
                .price(trip.getPrice())
                .passengerName(trip.getPassengerName())
                .driverName(trip.getDriverName())
                .build();
    }

    @Override
    public TripInfo toTripInfo(TripResponseDto trip) {
        return TripInfo.builder()
                .id(trip.getId())
                .status(trip.getStatus())
                .destinationAddress(trip.getDestinationAddress())
                .originAddress(trip.getOriginAddress())
                .price(trip.getPrice())
                .passengerName(trip.getPassengerName())
                .driverName(trip.getDriverName())
                .build();
    }
}
