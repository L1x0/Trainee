package by.astakhau.trainee.tripservice.data.dtos;

import by.astakhau.trainee.tripservice.data.entities.TripStatus;
import lombok.Data;

@Data
public class TripResponseDto {
    private TripStatus status;
    private String destinationAddress;
    private String originAddress;
    private Integer price;
    private String passengerName;
    private String driverName;
}
