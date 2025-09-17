package by.astakhau.trainee.driverservice.controllers;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping("/all")
    public ResponseEntity<Page<CarResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(carService.findAll(pageable));
    }

    @GetMapping
    public ResponseEntity<CarResponseDto> findById(@RequestParam Long id) {
        return ResponseEntity.of(carService.findById(id));
    }
}
