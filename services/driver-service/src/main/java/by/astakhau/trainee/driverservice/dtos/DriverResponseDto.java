package by.astakhau.trainee.driverservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isDeleted;
    private CarResponseDto car;
    private Boolean isBusy;
}
