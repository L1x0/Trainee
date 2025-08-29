package by.astakhau.trainee.ratingservice.services;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.mappers.RatingMapper;
import by.astakhau.trainee.ratingservice.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public Page<RatingResponseDto> findAll(Pageable pageable) {
        return ratingRepository.findAll(pageable).map(ratingMapper::ratingToRatingResponseDto);
    }

    public Page<RatingResponseDto> findByRaterRole(RaterRole raterRole, Pageable pageable) {
        return ratingRepository.findAllByRaterRole(raterRole, pageable).map(ratingMapper::ratingToRatingResponseDto);
    }

    @Transactional
    public RatingResponseDto update(Long raterId, RaterRole raterRole, @RequestBody RatingRequestDto ratingRequestDto) {
        var rating = ratingRepository.findByRaterRoleAndRaterId(raterRole, raterId);

        if (rating.isPresent()) {
            rating.get().setComment(ratingRequestDto.getComment());
            rating.get().setScore(ratingRequestDto.getScore());

            ratingRepository.save(rating.get());
        }

        return null;
    }

    @Transactional
    public RatingResponseDto createRating(RatingRequestDto ratingRequestDto) {
        var rating = ratingMapper.ratingRequestDtoToRating(ratingRequestDto);
        ratingRepository.save(rating);
        return ratingMapper.ratingToRatingResponseDto(rating);
    }

    @Transactional
    public RatingResponseDto updateRating(RatingRequestDto ratingRequestDto) {
        return createRating(ratingRequestDto);
    }

    @Transactional
    public void deleteRating(RaterRole raterRole, String raterComment) {
        ratingRepository.deleteByRaterRoleAndComment(raterRole, raterComment);
    }
}
