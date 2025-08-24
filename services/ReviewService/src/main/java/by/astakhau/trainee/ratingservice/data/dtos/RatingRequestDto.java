package by.astakhau.trainee.ratingservice.data.dtos;

import by.astakhau.trainee.ratingservice.data.entities.RaterRole;
import lombok.Data;

@Data
public class RatingRequestDto {
    private RaterRole raterRole;
    private Byte score;
    private String comment;
    private Long raterId;
}
