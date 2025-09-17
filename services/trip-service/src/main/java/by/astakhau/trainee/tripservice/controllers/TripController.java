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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trips")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class TripController {
    private final TripService tripService;

    @PostMapping("/create-trip")
    public ResponseEntity<TripResponseDto> createTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {
        var trip = tripService.createTrip(tripRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/trips")
                .queryParam("id", trip.getId())
                .build()
                .toUri();
        
        return ResponseEntity.created(location).body(trip);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TripResponseDto>> getAllTrips(Pageable pageable) {
        return ResponseEntity.ok(tripService.findAll(pageable));
    }

    @GetMapping("/accepted")
    public ResponseEntity<Page<TripResponseDto>> getActiveTrips(Pageable pageable) {
        return ResponseEntity.ok(tripService.findAllByStatus(pageable,  TripStatus.ACCEPTED));
    }

    @GetMapping
    public ResponseEntity<TripResponseDto> getTripById(@RequestParam(required = false) Long id) {
        return ResponseEntity.of(tripService.findById(id));
    }

    @PutMapping("/update")
    public ResponseEntity<TripResponseDto> updateTrip(
            @RequestParam(required = false) Long id,
            @Valid @RequestBody TripRequestDto tripRequestDto) {

        return ResponseEntity.ok(tripService.update(id, tripRequestDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTrip(@RequestParam(required = false) String destinationAddress,
                           @RequestParam(required = false) String driverName) {

        tripService.delete(driverName, destinationAddress);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accept")
    public ResponseEntity<Void> acceptTrip(@RequestParam(required = false) Long id) {

        tripService.changeStatus(id, TripStatus.ACCEPTED);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cansel")
    public ResponseEntity<Void> canselTrip(@RequestParam(required = false) Long id) {

        tripService.changeStatus(id, TripStatus.CANCELLED);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/en-route-to-pickup")
    public ResponseEntity<Void> pickUpTrip(@RequestParam(required = false) Long id) {

        tripService.changeStatus(id, TripStatus.EN_ROUTE_TO_PICKUP);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/en-route-to-destination")
    public ResponseEntity<Void> destinationTrip(@RequestParam(required = false) Long id) {

        tripService.changeStatus(id, TripStatus.EN_ROUTE_TO_DESTINATION);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/complete")
    public ResponseEntity<Void> changeStatus(@RequestParam(required = false) Long id) {

        tripService.endOfTrip(id);
        return ResponseEntity.noContent().build();
    }
}
