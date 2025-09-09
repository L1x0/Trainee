package by.astakhau.trainee.ratingservice.clients;

import by.astakhau.trainee.ratingservice.dtos.TripResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "trip-service")
public interface TripClient {
    @GetMapping("/trips")
    TripResponseDto findById(@RequestParam Long id);
}
