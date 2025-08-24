package by.astakhau.trainee.tripservice.data.dtos;

import by.astakhau.trainee.tripservice.data.entities.TripStatus;
import lombok.Data;

@Data
public class TripRequestDto {
    private String destinationAddress;
    private String originAddress;
    private String passengerName;
}
