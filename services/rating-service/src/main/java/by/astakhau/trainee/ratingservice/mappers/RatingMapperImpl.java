package by.astakhau.trainee.ratingservice.mappers;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.Rating;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class RatingMapperImpl implements RatingMapper {
    @Override
    public Rating ratingRequestDtoToRating(RatingRequestDto ratingRequestDto) {
        return Rating.builder()
                .id(null)
                .raterRole(ratingRequestDto.getRaterRole())
                .comment(ratingRequestDto.getComment())
                .score(ratingRequestDto.getScore())
                .tripId(ratingRequestDto.getTripId())
                .raterId(ratingRequestDto.getRaterId())
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Override
    public RatingResponseDto ratingToRatingResponseDto(Rating rating) {
        return RatingResponseDto.builder()
                .id(rating.getId())
                .comment(rating.getComment())
                .score(rating.getScore())
                .tripId(rating.getTripId())
                .raterId(rating.getRaterId())
                .raterRole(rating.getRaterRole())
                .build();
    }
}
