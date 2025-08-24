package by.astakhau.trainee.tripservice.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "trips")
@SQLDelete(sql = "UPDATE trips SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "passenger_id")
    private Long passengerId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "passenger_name")
    private String passengerName;

    @Column(name = "origin_address")
    private String originAddress;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    TripStatus status;

    @Column(name = "order_datetime")
    private OffsetDateTime orderDateTime;

    private Integer price;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
