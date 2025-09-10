package by.astakhau.trainee.driverservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverRequestDto {
    @NotNull
    @Size(min = 1, max = 250)
    private String name;

    @NotNull
    @Email
    private String email;

    @Pattern(regexp = "\\+?\\d{9,15}")
    private String phoneNumber;

    private CarRequestDto car;
}
