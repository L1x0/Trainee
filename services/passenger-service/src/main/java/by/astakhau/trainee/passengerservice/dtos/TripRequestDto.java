package by.astakhau.trainee.passengerservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripRequestDto {
    private long id;

    @NotNull
    @Size(min = 1, max = 250)
    private String passengerName;

    @NotNull
    @Pattern(regexp = "\\+?\\d{9,15}")
    private String passengerPhoneNumber;

    @NotNull
    @Size(min = 1, max = 500)
    private String destinationAddress;

    @NotNull
    @Size(min = 1, max = 500)
    private String originAddress;
}
