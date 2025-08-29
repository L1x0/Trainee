package by.astakhau.trainee.ratingservice.dtos;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingRequestDto {
    @NotNull
    @Size(min = 6, max = 9)
    private RaterRole raterRole;

    @Max(500)
    private Byte score;

    @Max(500)
    private String comment;
    private Long raterId;
}
