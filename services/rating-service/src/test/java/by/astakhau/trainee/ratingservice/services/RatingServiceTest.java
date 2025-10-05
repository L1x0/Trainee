package by.astakhau.trainee.ratingservice.services;

import by.astakhau.trainee.ratingservice.clients.TripClient;
import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.dtos.TripResponseDto;
import by.astakhau.trainee.ratingservice.dtos.TripStatus;
import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.entities.Rating;
import by.astakhau.trainee.ratingservice.mappers.RatingMapper;
import by.astakhau.trainee.ratingservice.repositories.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @InjectMocks
    private RatingService ratingService;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingMapper ratingMapper;
    @Mock
    TripClient tripClient;

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());

        Rating r1 = new Rating();
        r1.setId(1L);
        Rating r2 = new Rating();
        r2.setId(2L);

        Page<Rating> page = new PageImpl<>(List.of(r1, r2), pageable, 2);
        when(ratingRepository.findAll(pageable)).thenReturn(page);

        RatingResponseDto dto1 = RatingResponseDto.builder().id(1L).build();
        RatingResponseDto dto2 = RatingResponseDto.builder().id(2L).build();

        when(ratingMapper.ratingToRatingResponseDto(r1)).thenReturn(dto1);
        when(ratingMapper.ratingToRatingResponseDto(r2)).thenReturn(dto2);

        Page<RatingResponseDto> result = ratingService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(ratingRepository).findAll(pageable);
        verify(ratingMapper).ratingToRatingResponseDto(r1);
        verify(ratingMapper).ratingToRatingResponseDto(r2);
        verifyNoMoreInteractions(ratingRepository, ratingMapper);
    }

    @Test
    void findByRaterRole() {
        Pageable pageable = PageRequest.of(0, 10);
        RaterRole role = RaterRole.PASSENGER;

        Rating r = new Rating();
        r.setId(10L);
        Page<Rating> page = new PageImpl<>(List.of(r), pageable, 1);
        when(ratingRepository.findAllByRaterRole(role, pageable)).thenReturn(page);

        RatingResponseDto dto = RatingResponseDto.builder().id(10L).build();
        when(ratingMapper.ratingToRatingResponseDto(r)).thenReturn(dto);

        Page<RatingResponseDto> result = ratingService.findByRaterRole(role, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(10L, result.getContent().get(0).getId());

        verify(ratingRepository).findAllByRaterRole(role, pageable);
        verify(ratingMapper).ratingToRatingResponseDto(r);
        verifyNoMoreInteractions(ratingRepository, ratingMapper);
    }

    @Test
    void update() {

        when(ratingRepository.findByRaterRoleAndRaterId(any(RaterRole.class), anyLong()))
                .thenReturn(Optional.ofNullable(Rating.builder().id(1L).build()));

        var rating = Rating.builder()
                .comment("comment")
                .score((byte) 3)
                .build();

        var ratingDto = RatingResponseDto.builder()
                .comment("comment")
                .score((byte) 3)
                .build();

        when(ratingRepository.save(any())).thenReturn(rating);
        when(ratingMapper.ratingToRatingResponseDto(any(Rating.class))).thenReturn(ratingDto);

        var result = ratingService.update(1L, RaterRole.PASSENGER, RatingRequestDto.builder()
                .comment("comment")
                .score((byte) 3)
                .build());

        assertEquals("comment", result.getComment());
        assertEquals((byte) 3, result.getScore());
        verifyNoMoreInteractions(ratingRepository, ratingMapper);
    }

    @Test
    void createRating() {
        when(tripClient.findById(anyLong()))
                .thenReturn(TripResponseDto.builder().status(TripStatus.COMPLETED).build());

        var ratingRequestDto = RatingRequestDto.builder()
                .raterId(1L)
                .tripId(2L)
                .raterRole(RaterRole.PASSENGER)
                .score((byte) 3)
                .comment("comment")
                .build();

        var rating = Rating.builder()
                .tripId(1L)
                .createdAt(OffsetDateTime.now())
                .raterId(ratingRequestDto.getRaterId())
                .raterRole(ratingRequestDto.getRaterRole())
                .score((byte) 3)
                .comment("comment")
                .tripId(ratingRequestDto.getTripId())
                .build();

        when(ratingMapper.ratingRequestDtoToRating(any(RatingRequestDto.class))).thenReturn(rating);

        var ratingResponseDto = RatingResponseDto.builder()
                .score((byte) 3)
                .comment("comment")
                .tripId(ratingRequestDto.getTripId())
                .id(ratingRequestDto.getRaterId())
                .raterId(ratingRequestDto.getRaterId())
                .raterRole(ratingRequestDto.getRaterRole())
                .build();

        when(ratingMapper.ratingToRatingResponseDto(any(Rating.class))).thenReturn(ratingResponseDto);

        assertEquals(ratingResponseDto, ratingService.createRating(ratingRequestDto));
        verify(ratingRepository).save(rating);
    }

    @Test
    void deleteRating() {
        ratingService.deleteRating(RaterRole.PASSENGER, "comment");
        verify(ratingRepository).deleteByRaterRoleAndComment(String.valueOf(RaterRole.PASSENGER), "comment");
    }
}
