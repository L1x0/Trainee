package by.astakhau.trainee.ratingservice.repositories;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findAllByRaterRole(RaterRole raterRole, Pageable pageable);
    void deleteByRaterRoleAndComment(RaterRole raterRole, String raterComment);

    Optional<Rating> findByRaterRoleAndRaterId(RaterRole raterRole, Long raterId);
}
