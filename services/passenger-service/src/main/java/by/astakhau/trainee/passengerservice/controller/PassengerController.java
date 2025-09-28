package by.astakhau.trainee.passengerservice.controller;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.services.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Empty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping("/create")
    public ResponseEntity<PassengerResponseDto> createPassenger(@Valid @RequestBody PassengerRequestDto passengerRequestDto) {
        var passenger = passengerService.savePassenger(passengerRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/passenger")
                .queryParam("id", passenger.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(params = "id")
    public ResponseEntity<PassengerResponseDto> findById(@RequestParam(required = false) Long id) {
        return ResponseEntity.of(passengerService.findById(id));
    }

    @GetMapping("/all")
    public Page<PassengerResponseDto> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return passengerService.findAll(pageable);
    }

    // GET /passengers?name=Артём Астахов&page=0&size=20&sort=name,asc
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PassengerResponseDto>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(passengerService.findAllByName(name, pageable));
    }

    @PutMapping("/update")
    public ResponseEntity<PassengerResponseDto> updatePassenger(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @Valid @RequestBody PassengerRequestDto passengerRequestDto) {

        return ResponseEntity.ok(passengerService.update(name, phoneNumber, passengerRequestDto));
    }

    @DeleteMapping(value = "/delete", params = "email")
    public ResponseEntity<Void> deleteByEmail(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        passengerService.deleteWithEmail(name, email);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/create-order")
    public ResponseEntity<Empty> createOrder(@Valid @RequestBody TripRequestDto tripRequestDto) {
        passengerService.createTripOrder(tripRequestDto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-trip-info")
    public ResponseEntity<TripResponseDto> getTripInfo(@Valid @RequestParam String passengerName) {
        return ResponseEntity.ok(passengerService.getTripInfo(passengerName));
    }
}
