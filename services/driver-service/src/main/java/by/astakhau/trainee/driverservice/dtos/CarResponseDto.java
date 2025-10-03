package by.astakhau.trainee.driverservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {
    @NotNull
    @Size(min = 1, max = 150)
    private String make;

    @NotNull
    @Size(min = 1, max = 50)
    private String color;

    @NotNull
    @Pattern(
            regexp = "^(?:[0-9]{4} ?[AEIOBCHKPTX]{2}-[0-9]|E[0-9]{3} ?[AEIOBCHKPTX]{2}-[0-9]|[AEIOBCHKPTX]{2} ?[0-9]{4}-[0-9]|[AEIOBCHKPTX][0-9]{4}[AEIOBCHKPTX]-[0-9])$",
            message = "Invalid plate number format"
    )
    private String plateNumber;
    private OffsetDateTime createdAt;
    private Boolean isDeleted;
}
