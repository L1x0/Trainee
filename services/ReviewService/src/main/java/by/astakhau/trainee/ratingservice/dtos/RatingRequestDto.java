package by.astakhau.trainee.ratingservice.dtos;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import lombok.Data;

@Data
public class RatingRequestDto {
    private RaterRole raterRole;
    private Byte score;
    private String comment;
    private Long raterId;
}
