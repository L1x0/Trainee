package by.astakhau.trainee.driverservice.controllers;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.dtos.TripDto;
import by.astakhau.trainee.driverservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/create")
    public ResponseEntity<DriverResponseDto> createDriver(@Valid @RequestBody DriverRequestDto dto) {
        var driver = driverService.save(dto);

        URI location = UriComponentsBuilder.fromPath("/drivers").buildAndExpand(dto).toUri();

        return ResponseEntity.created(location).body(driver);
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponseDto>> getAllDrivers(Pageable pageable) {
        return ResponseEntity.ok(driverService.findAll(pageable));
    }

    @GetMapping(params = "id")
    public ResponseEntity<DriverResponseDto> getDriverById(@RequestParam(required = false) Long id) {
        return ResponseEntity.of(driverService.findById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<DriverResponseDto> updateDriver(@Valid @RequestBody DriverRequestDto dto) {
        return ResponseEntity.ok(driverService.update(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDriver(@RequestParam(required = false) String name, @RequestParam(required = false) String email) {
        driverService.deleteByNameAndEmail(name, email);

        return ResponseEntity.noContent().build();
    }
}
