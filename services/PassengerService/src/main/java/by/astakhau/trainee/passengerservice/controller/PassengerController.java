package by.astakhau.trainee.passengerservice.controller;

import by.astakhau.trainee.passengerservice.data.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.data.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.data.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.services.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping("/create")
    public void createPassenger(@RequestBody PassengerRequestDto passengerRequestDto) {
        passengerService.savePassenger(passengerRequestDto);
    }

    @GetMapping("/{id}")
    public PassengerResponseDto findById(@PathVariable Long id) {
        return passengerService.findById(id);
    }

    @GetMapping("/all")
    public Page<PassengerResponseDto> findAll(@PageableDefault(size = 20) Pageable pageable) {
        return passengerService.findAll(pageable);
    }

    // GET /passengers?name=Артём Астахов&page=0&size=20&sort=name,asc
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<PassengerResponseDto> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {

        return passengerService.findAllByName(name, pageable);
    }

    @PutMapping("/update")
    public void updatePassenger(@RequestBody PassengerRequestDto passengerRequestDto) {
        passengerService.savePassenger(passengerRequestDto);
    }

    @DeleteMapping(value = "/delete", params = "email")
    public void deleteByEmail(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {

        passengerService.deleteWithEmail(name, email);
    }


    @PostMapping("/create-order")
    public void createOrder(@RequestBody TripRequestDto tripRequestDto) {
        passengerService.createTripOrder(tripRequestDto);
    }
}
