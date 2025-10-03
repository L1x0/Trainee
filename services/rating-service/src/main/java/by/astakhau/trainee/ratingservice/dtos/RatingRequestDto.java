package by.astakhau.trainee.ratingservice.dtos;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingRequestDto {
    @NotNull
    private RaterRole raterRole;

    @Max(5)
    @Nullable
    private Byte score;

    @Size(min = 1, max = 500)
    @Nullable
    private String comment;

    @NotNull
    private Long raterId;

    @NotNull
    private Long tripId;
}
