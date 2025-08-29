package by.astakhau.trainee.driverservice.controllers;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<CarResponseDto> findAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping
    public CarResponseDto findById(@RequestParam Long id) {
        return carService.findById(id);
    }
}
