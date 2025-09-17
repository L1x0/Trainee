package by.astakhau.trainee.ratingservice.dtos;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponseDto {
    private Long id;
    private Long tripId;
    private Long raterId;
    private RaterRole raterRole;
    private Byte score;
    private String comment;
}
