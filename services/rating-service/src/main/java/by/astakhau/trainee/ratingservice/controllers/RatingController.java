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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Validated
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/all")
    public ResponseEntity<Page<RatingResponseDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ratingService.findAll(pageable));
    }

    @GetMapping("/drivers-review")
    public ResponseEntity<Page<RatingResponseDto>> findAllDriversReview(Pageable pageable) {
        return ResponseEntity.ok(ratingService.findByRaterRole(RaterRole.DRIVER, pageable));
    }

    @GetMapping("/passengers-review")
    public ResponseEntity<Page<RatingResponseDto>> findAllPassengersReview(Pageable pageable) {
        return ResponseEntity.ok(ratingService.findByRaterRole(RaterRole.PASSENGER, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<RatingResponseDto> createRating(@Valid @RequestBody RatingRequestDto ratingRequestDto) {
        var rating = ratingService.createRating(ratingRequestDto);

//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath()
//                .path("/rating")
//                .queryParam("id", rating.getId())
//                .build()
//                .toUri();

        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<RatingResponseDto> updateRating(
            @RequestParam(required = false) Long raterId,
            @RequestParam(required = false) RaterRole raterRole,
            @Valid @RequestBody RatingRequestDto ratingRequestDto) {
        return ResponseEntity.ok(ratingService.update(raterId, raterRole, ratingRequestDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRating(@RequestParam(required = false) RaterRole raterRole,
                             @RequestParam(required = false) String raterComment) {

        ratingService.deleteRating(raterRole, raterComment);

        return ResponseEntity.noContent().build();
    }
}
