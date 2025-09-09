package by.astakhau.trainee.passengerservice.client;

import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trip-service")
public interface TripClient {
    @PostMapping("/trips/create-trip")
    void createTrip(@RequestBody TripRequestDto tripRequestDto);
}
