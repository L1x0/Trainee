package by.astakhau.trainee.ratingservice.mappers;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    public Rating ratingRequestDtoToRating(RatingRequestDto ratingRequestDto);
    public RatingRequestDto ratingToRatingRequestDto(Rating rating);

    public Rating ratingResponseDtoToRating(RatingResponseDto ratingResponseDto);
    public RatingResponseDto ratingToRatingResponseDto(Rating rating);
}
