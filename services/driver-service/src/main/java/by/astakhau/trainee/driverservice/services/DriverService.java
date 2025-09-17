package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.mappers.CarMapper;
import by.astakhau.trainee.driverservice.mappers.DriverMapper;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarMapper carMapper;


    @Transactional
    public DriverResponseDto save(DriverRequestDto driverRequestDto) {
        var driver = driverMapper.fromRequestDto(driverRequestDto);
        log.info("trying to save Driver: {}", driver);

        return driverMapper.driverToDriverResponseDto(driverRepository.save(driver));
    }

    public DriverResponseDto update(DriverRequestDto driverRequestDto) {
        var driver = driverRepository.findByEmail(driverRequestDto.getEmail());

        if (driver.isPresent()) {

            driver.get().setName(driverRequestDto.getName());
            driver.get().setPhoneNumber(driverRequestDto.getPhoneNumber());

            log.info("Updated Driver: {}", driver);

            return driverMapper.driverToDriverResponseDto(driverRepository.save(driver.get()));
        }

        log.error("Driver is not found");
        throw new IllegalStateException("Driver hasn't been saved before");
    }

    public Page<DriverResponseDto> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::driverToDriverResponseDto);
    }


    public Optional<DriverResponseDto> findById(Long id) {
        return driverRepository.findById(id).map(driverMapper::driverToDriverResponseDto);
    }

    @Transactional
    public void deleteByNameAndEmail(String name, String email) {
        driverRepository.softDeleteByNameAndEmail(name, email);

        log.info("Driver with name {} and email {} deleted", name, email);
    }
}
