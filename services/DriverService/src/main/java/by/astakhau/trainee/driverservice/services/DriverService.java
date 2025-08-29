package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.entities.Driver;
import by.astakhau.trainee.driverservice.mappers.CarMapper;
import by.astakhau.trainee.driverservice.mappers.DriverMapper;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarService carService;
    private final CarMapper carMapper;


    @Transactional
    public void save(DriverRequestDto driverRequestDto) {
        Driver driver = driverMapper.fromRequestDto(driverRequestDto);

        driver.setCreatedAt(OffsetDateTime.now());
        driver.setIsDeleted(false);
        driver.setDeletedAt(null);

        var car = carMapper.fromRequestDto(driverRequestDto.getCar());

        car.setCreatedAt(OffsetDateTime.now());
        car.setIsDeleted(false);
        car.setDeletedAt(null);

        driver.setCar(car);
        car.setDriver(driver);

        driverRepository.save(driver);
    }

    public void update(DriverRequestDto driverRequestDto) {
        var driver = driverRepository.findByEmail(driverRequestDto.getEmail());

        if (driver.isPresent()) {
            driver.get().setEmail(driverRequestDto.getEmail());
            driver.get().setName(driverRequestDto.getName());
            driver.get().setPhoneNumber(driverRequestDto.getPhoneNumber());


            driverRepository.save(driver.get());
        }
    }

    public Page<DriverResponseDto> findAll(Pageable pageable) {
        var result = driverRepository.findAll(pageable);

        return driverRepository.findAll(pageable).map(driverMapper::driverToDriverResponseDto);
    }


    public DriverResponseDto findById(Long id) {
        Driver driver = driverRepository.findById(id).orElse(null);

        return driverMapper.driverToDriverResponseDto(driver);
    }

    @Transactional
    public void deleteByNameAndEmail(String name, String email) {
        driverRepository.softDeleteByNameAndEmail(name, email);
    }

    //функция подбора свободного водителя для создания нового заказа
    public TripDto getDriverForTrip() {
        return driverRepository.findFirstById(1).map(driverMapper::DriverToTripDto).orElse(null);
    }
}
