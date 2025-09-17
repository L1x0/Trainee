package by.astakhau.trainee.passengerservice.client;

import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trip-service")
public interface TripClient {
    @PostMapping("/trips/create-trip")
    TripResponseDto createTrip(@RequestBody TripRequestDto tripRequestDto);
}
