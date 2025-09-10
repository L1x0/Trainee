package by.astakhau.trainee.driverservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripDto {
    private long id;
    private String name;
}
