package by.astakhau.trainee.driverservice.integration;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.services.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DriverServiceRepositoryIT extends AbstractIntegrationTest {
    @Autowired
    private DriverService driverService;

    @Test
    @Transactional
    @Rollback
    public void save_and_findTest() {
        var driverRequestDto = DriverRequestDto.builder()
                .name("Driver Request")
                .email("artemasyahov27@gmail.com")
                .phoneNumber("+375447006485")
                .car(CarRequestDto.builder()
                        .make("mersedes")
                        .color("white")
                        .plateNumber("0000AA-5")
                        .build())
                .build();

        var driver = driverService.save(driverRequestDto);

        var result = driverService.findById(driver.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(driverRequestDto.getName());
        assertThat(result.get().getCar().getPlateNumber()).isEqualTo(driverRequestDto.getCar().getPlateNumber());
    }

    @Test
    @Transactional
    @Rollback
    public void softDelete_and_findTest() {
        var driverRequestDto = DriverRequestDto.builder()
                .name("Driver Request")
                .email("artemasyahov27@gmail.com")
                .phoneNumber("+375447006485")
                .car(CarRequestDto.builder()
                        .make("mersedes")
                        .color("white")
                        .plateNumber("0000AA-5")
                        .build())
                .build();

        var driver = driverService.save(driverRequestDto);

        driverService.deleteByNameAndEmail(driver.getName(), driver.getEmail());

        var result = driverService.findById(driver.getId());

        assertThat(result).isEmpty();
    }
}
