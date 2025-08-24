package by.astakhau.trainee.driverservice.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "cars")
@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_car_driver"))
    private Driver driver;

    private String make;

    private String color;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Version
    private Integer version;
}
