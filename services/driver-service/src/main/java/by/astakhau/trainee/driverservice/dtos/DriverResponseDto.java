package by.astakhau.trainee.driverservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponseDto {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isDeleted;
    private CarResponseDto car;
    private Boolean isBusy;
}
