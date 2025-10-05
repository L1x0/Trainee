package by.astakhau.trainee.ratingservice.repositories;

import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Page<Rating> findAllByRaterRole(RaterRole raterRole, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM trip_rating WHERE rater_role = ?1 AND comment = ?2", nativeQuery = true)
    int deleteByRaterRoleAndComment(String raterRoleName, String raterComment);

    Optional<Rating> findByRaterRoleAndRaterId(RaterRole raterRole, Long raterId);
}
