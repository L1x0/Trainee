package by.astakhau.trainee.tripservice.controllers;

import by.astakhau.trainee.tripservice.dtos.PassengerOrderDto;
import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class TripController {
    private final TripService tripService;

    @PostMapping("/update")
    public void createTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {
        tripService.save(tripRequestDto);
    }

    @GetMapping("/all")
    public Page<TripResponseDto> getAllTrips(Pageable pageable) {
        return tripService.findAll(pageable);
    }

    @GetMapping("/accepted")
    public Page<TripResponseDto> getActiveTrips(Pageable pageable) {
        return tripService.findAllByStatus(pageable,  TripStatus.ACCEPTED);
    }

    @GetMapping
    public TripResponseDto getTripById(@RequestParam(required = false) Long id) {
        return tripService.findById(id);
    }

    @PutMapping("/update")
    public TripResponseDto updateTrip(
            @RequestParam(required = false) String passengerName,
            @RequestParam(required = false) String driverName
            ,@Valid @RequestBody TripRequestDto tripRequestDto) {

        return tripService.update(passengerName, driverName, tripRequestDto);
    }

    @DeleteMapping
    public void deleteTrip(@RequestParam(required = false) String destinationAddress,
                           @RequestParam(required = false) String driverName) {

        tripService.delete(driverName, destinationAddress);
    }

    @PutMapping
    public void changeStatus(@RequestParam(required = false) TripStatus status,
                             @RequestParam(required = false) String driverName,
                             @RequestParam(required = false) String passengerName) {

        tripService.changeStatus(driverName, passengerName, status);
    }
}
