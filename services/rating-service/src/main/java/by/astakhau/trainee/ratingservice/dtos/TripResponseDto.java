package by.astakhau.trainee.ratingservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripResponseDto {
    private TripStatus status;
    private String destinationAddress;
    private String originAddress;
    private Integer price;
    private String passengerName;
    private String driverName;
}
