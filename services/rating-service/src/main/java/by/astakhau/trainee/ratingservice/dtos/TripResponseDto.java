package by.astakhau.trainee.ratingservice.dtos;

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
