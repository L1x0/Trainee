package by.astakhau.trainee.ratingservice.mappers;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.Rating;


public interface RatingMapper {
    Rating ratingRequestDtoToRating(RatingRequestDto ratingRequestDto);
    RatingResponseDto ratingToRatingResponseDto(Rating rating);
}
