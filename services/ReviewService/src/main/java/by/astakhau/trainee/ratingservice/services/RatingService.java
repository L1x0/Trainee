package by.astakhau.trainee.ratingservice.services;

import by.astakhau.trainee.ratingservice.data.mappers.RatingMapper;
import by.astakhau.trainee.ratingservice.data.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
}
