package by.astakhau.trainee.passengerservice.dtos;

import by.astakhau.trainee.passengerservice.entities.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {
    private Long id;
    private TripStatus status;
    private String destinationAddress;
    private String originAddress;
    private Integer price;
    private String passengerName;
    private String driverName;
}
