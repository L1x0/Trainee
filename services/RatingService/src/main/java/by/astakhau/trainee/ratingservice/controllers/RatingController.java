package by.astakhau.trainee.ratingservice.controllers;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/all")
    public Page<RatingResponseDto> findAll(Pageable pageable) {
        return ratingService.findAll(pageable);
    }

    @GetMapping("/drivers-review")
    public Page<RatingResponseDto> findAllDriversReview(Pageable pageable) {
        return ratingService.findByRaterRole(RaterRole.DRIVER, pageable);
    }

    @GetMapping("/passengers-review")
    public Page<RatingResponseDto> findAllPassengersReview(Pageable pageable) {
        return ratingService.findByRaterRole(RaterRole.PASSENGER, pageable);
    }

    @PostMapping("/create")
    public RatingResponseDto createRating(@Valid @RequestBody RatingRequestDto ratingRequestDto) {
        return ratingService.createRating(ratingRequestDto);
    }

    @PutMapping("/update")
    public RatingResponseDto updateRating(
            @RequestParam(required = false) Long raterId,
            @RequestParam(required = false) RaterRole raterRole,
            @Valid @RequestBody RatingRequestDto ratingRequestDto) {
        return ratingService.update(raterId, raterRole, ratingRequestDto);
    }

    @DeleteMapping("/delete")
    public void deleteRating(@RequestParam(required = false) RaterRole raterRole,
                             @RequestParam(required = false) String raterComment) {

        ratingService.deleteRating(raterRole, raterComment);
    }
}
