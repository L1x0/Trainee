package by.astakhau.trainee.passengerservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PassengerRequestDto {
    @NotNull
    @Size(min = 1, max = 250)
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "\\+?\\d{9,15}")
    private String phoneNumber;
}
