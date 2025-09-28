package by.astakhau.trainee.passengerservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trips")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripInfo {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "origin_address")
    private String originAddress;

    private Integer price;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "passenger_name")
    private String passengerName;
}
