package by.astakhau.trainee.tripservice.dtos;

import by.astakhau.trainee.tripservice.entities.TripStatus;
import lombok.Data;

@Data
public class TripResponseDto {
    private Long id;
    private TripStatus status;
    private String destinationAddress;
    private String originAddress;
    private Integer price;
    private String passengerName;
    private String driverName;
}
