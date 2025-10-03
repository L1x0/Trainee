package by.astakhau.trainee.ratingservice.dtos;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {
    private Long id;
    private Long tripId;
    private Long raterId;
    private RaterRole raterRole;
    private Byte score;
    private String comment;
}
