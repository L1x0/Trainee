package by.astakhau.trainee.tripservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TripRequestDto {
    @NotNull
    @Size(min = 1, max = 500)
    private String destinationAddress;

    @NotNull
    @Size(min = 1, max = 500)
    private String originAddress;

    @NotNull
    @Size(min = 1, max = 250)
    private String passengerName;
}
