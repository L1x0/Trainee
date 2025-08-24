package by.astakhau.trainee.driverservice.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "drivers")
@SQLDelete(sql = "UPDATE drivers SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Version
    private Integer version;

    @OneToOne(mappedBy = "driver")
    private Car car;
}
