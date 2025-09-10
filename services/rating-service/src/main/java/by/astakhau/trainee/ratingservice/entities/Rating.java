package by.astakhau.trainee.ratingservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "trip_rating")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id")
    private Long tripId;

    @Column(name = "rater_id")
    private Long raterId;

    @Enumerated(EnumType.STRING)
    private RaterRole raterRole;

    @Column(name = "score", nullable = true)
    private Byte score;

    @Column(name = "comment", nullable = true)
    private String comment;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
