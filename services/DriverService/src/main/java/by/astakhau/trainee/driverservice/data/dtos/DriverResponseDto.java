package by.astakhau.trainee.driverservice.data.dtos;

import lombok.Data;

@Data
public class DriverResponseDto {
    private String name;
    private String email;
    private String phoneNumber;
    private Boolean isDeleted;
}
