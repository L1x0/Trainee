package by.astakhau.trainee.ratingservice.data.repositories;

import by.astakhau.trainee.ratingservice.data.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
