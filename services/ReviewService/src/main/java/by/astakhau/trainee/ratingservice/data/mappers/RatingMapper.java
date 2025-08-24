package by.astakhau.trainee.ratingservice.data.mappers;

import by.astakhau.trainee.ratingservice.data.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.data.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.data.entities.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    public Rating ratingRequestDtoToRating(RatingRequestDto ratingRequestDto);
    public RatingRequestDto ratingToRatingDto(Rating rating);

    public Rating ratingResponseDtoToRating(RatingResponseDto ratingResponseDto);
    public RatingResponseDto ratingToRatingResponseDto(Rating rating);
}
