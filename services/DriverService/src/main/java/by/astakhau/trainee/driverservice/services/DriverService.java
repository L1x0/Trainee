package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.data.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.data.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.data.entities.Driver;
import by.astakhau.trainee.driverservice.data.mappers.DriverMapper;
import by.astakhau.trainee.driverservice.data.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;


    @Transactional
    public void save(DriverRequestDto driverRequestDto) {
        Driver driver = driverMapper.fromRequestDto(driverRequestDto);

        driver.setCreatedAt(OffsetDateTime.now());
        driver.setIsDeleted(false);
        driver.setDeletedAt(null);

        driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDto> findAll() {
        List<Driver> result = driverRepository.findAll();

        List<DriverResponseDto> driverResponseDtos = new ArrayList<>();

        result.forEach(driver -> {
            driverResponseDtos.add(driverMapper.driverToDriverResponseDto(driver));
        });

        return driverResponseDtos;
    }

    @Transactional(readOnly = true)
    public DriverResponseDto findById(Long id) {
        Driver driver = driverRepository.findById(id).orElse(null);

        return driverMapper.driverToDriverResponseDto(driver);
    }

    @Transactional
    public void deleteById(Long id) {
        driverRepository.deleteById(id);
    }
}
