package by.astakhau.trainee.driverservice.dtos;

import lombok.Data;

@Data
public class DriverResponseDto {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isDeleted;
    private CarResponseDto car;
}
