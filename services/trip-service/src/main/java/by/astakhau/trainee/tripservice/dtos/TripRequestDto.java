package by.astakhau.trainee.tripservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TripRequestDto {
    private long id;

    @NotNull
    @Size(min = 1, max = 250)
    private String name;

    @NotNull
    @Pattern(regexp = "\\+?\\d{9,15}")
    private String phoneNumber;

    @NotNull
    @Size(min = 1, max = 500)
    private String destinationAddress;

    @NotNull
    @Size(min = 1, max = 500)
    private String originAddress;
}
