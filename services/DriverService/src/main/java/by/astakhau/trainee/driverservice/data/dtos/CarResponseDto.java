package by.astakhau.trainee.driverservice.data.dtos;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CarResponseDto {
    private String make;
    private String color;
    private String plateNumber;
    private OffsetDateTime createdAt;
    private Boolean isDeleted;
}
