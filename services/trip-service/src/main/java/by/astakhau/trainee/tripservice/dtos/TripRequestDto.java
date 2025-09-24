package by.astakhau.trainee.tripservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDto {
    private long PassengerId;

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
