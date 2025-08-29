package by.astakhau.trainee.tripservice.dtos;

import lombok.Data;

@Data
public class TripRequestDto {
    private String destinationAddress;
    private String originAddress;
    private String passengerName;
}
