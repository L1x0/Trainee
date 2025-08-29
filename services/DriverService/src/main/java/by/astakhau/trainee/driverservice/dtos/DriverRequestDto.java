package by.astakhau.trainee.driverservice.dtos;

import lombok.Data;

@Data
public class DriverRequestDto {
    private String name;
    private String email;
    private String phoneNumber;
    private CarRequestDto car;
}
