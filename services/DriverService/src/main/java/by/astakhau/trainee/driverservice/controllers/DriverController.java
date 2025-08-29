package by.astakhau.trainee.driverservice.controllers;

import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/create")
    public void createDriver(@Valid @RequestBody DriverRequestDto dto) {
        driverService.save(dto);
    }

    @GetMapping
    public Page<DriverResponseDto> getAllDrivers(Pageable pageable) {
        return driverService.findAll(pageable);
    }

    @GetMapping(params = "id")
    public DriverResponseDto getDriverById(@RequestParam(required = false) Long id) {
        return driverService.findById(id);
    }

    @PutMapping("/update")
    public void updateDriver(@RequestParam(required = false) String email, @Valid @RequestBody DriverRequestDto dto) {
        driverService.update(dto);
    }

    @DeleteMapping
    public void deleteDriver(@RequestParam(required = false) String name, @RequestParam(required = false) String email) {
        driverService.deleteByNameAndEmail(name, email);
    }

    @GetMapping("/driver-for-trip")
    public TripDto getDriverForTrip() {
        return driverService.getDriverForTrip();
    }
}
