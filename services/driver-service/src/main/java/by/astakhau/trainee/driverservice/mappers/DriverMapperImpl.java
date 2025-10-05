package by.astakhau.trainee.driverservice.mappers;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.entities.Driver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DriverMapperImpl implements DriverMapper {
    private final CarMapper carMapper;

    @Override
    public DriverResponseDto driverToDriverResponseDto(Driver driver) {
        return DriverResponseDto.builder()
                .id(driver.getId())
                .email(driver.getEmail())
                .car(carMapper.carToCarResponseDto(driver.getCar()))
                .isBusy(driver.getIsBusy())
                .name(driver.getName())
                .email(driver.getEmail())
                .phoneNumber(driver.getPhoneNumber())
                .isDeleted(driver.getIsDeleted())
                .build();
    }

    @Override
    public Driver fromRequestDto(DriverRequestDto driverRequestDto) {
        Driver driver =  Driver.builder()
                .id(null)
                .createdAt(OffsetDateTime.now())
                .deletedAt(null)
                .isBusy(false)
                .email(driverRequestDto.getEmail())
                .isDeleted(false)
                .version(0)
                .phoneNumber(driverRequestDto.getPhoneNumber())
                .name(driverRequestDto.getName())
                .car(carMapper.fromRequestDto(driverRequestDto.getCar(), null))
                .build();

        driver.getCar().setDriver(driver);
        return driver;
    }

    @Override
    public TripDto DriverToTripDto(Driver driver) {
        return TripDto.builder()
                .name(driver.getName())
                .id(driver.getId())
                .build();
    }
}
