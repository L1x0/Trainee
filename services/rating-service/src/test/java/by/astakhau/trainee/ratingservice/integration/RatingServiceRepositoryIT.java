package by.astakhau.trainee.ratingservice.integration;


import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.TripResponseDto;
import by.astakhau.trainee.ratingservice.dtos.TripStatus;
import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.repositories.RatingRepository;
import by.astakhau.trainee.ratingservice.services.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RatingServiceRepositoryIT extends AbstractIntegrationTest {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    RatingService ratingService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Transactional
    @Rollback
    public void saveRating() throws JsonProcessingException {
        TripResponseDto responseDto = TripResponseDto.builder()
                .status(TripStatus.COMPLETED)
                .destinationAddress("Destination Address")
                .originAddress("Origin Address")
                .driverName("Driver Name")
                .passengerName("Passenger Name")
                .price(100)
                .build();

        AbstractIntegrationTest.WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/trips"))
                .withQueryParam("id", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(responseDto))));


        ratingService.createRating(RatingRequestDto.builder()
                .raterRole(RaterRole.PASSENGER)
                .tripId(100L)
                .score((byte) 3)
                .raterId(100L)
                .comment("Comment")
                .build());

        var rating = ratingRepository.findByRaterRoleAndRaterId(RaterRole.PASSENGER, 100L);
        assertThat(rating).isPresent();
        assertThat(rating.get().getScore()).isEqualTo((byte) 3);
    }

    @Test
    @Transactional
    @Rollback
    public void deleteRating() throws JsonProcessingException {
        TripResponseDto responseDto = TripResponseDto.builder()
                .status(TripStatus.COMPLETED)
                .destinationAddress("Destination Address")
                .originAddress("Origin Address")
                .driverName("Driver Name")
                .passengerName("Passenger Name")
                .price(100)
                .build();

        AbstractIntegrationTest.WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/trips"))
                .withQueryParam("id", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(responseDto))));


        ratingService.createRating(RatingRequestDto.builder()
                .raterRole(RaterRole.PASSENGER)
                .tripId(100L)
                .score((byte) 3)
                .raterId(100L)
                .comment("Comment")
                .build());

        ratingService.deleteRating(RaterRole.PASSENGER, "Comment");

        var result = ratingRepository.findByRaterRoleAndRaterId(RaterRole.PASSENGER, 100L);
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    public void updateRating() throws JsonProcessingException {
        TripResponseDto responseDto = TripResponseDto.builder()
                .status(TripStatus.COMPLETED)
                .destinationAddress("Destination Address")
                .originAddress("Origin Address")
                .driverName("Driver Name")
                .passengerName("Passenger Name")
                .price(100)
                .build();

        AbstractIntegrationTest.WIREMOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/trips"))
                .withQueryParam("id", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(responseDto))));


        ratingService.createRating(RatingRequestDto.builder()
                .raterRole(RaterRole.PASSENGER)
                .tripId(100L)
                .score((byte) 3)
                .raterId(100L)
                .comment("Comment")
                .build());

        ratingService.update(100L, RaterRole.PASSENGER, RatingRequestDto.builder()
                .raterRole(RaterRole.PASSENGER)
                .tripId(100L)
                .score((byte) 5)
                .raterId(100L)
                .comment("Comment updated")
                .build());

        var result = ratingRepository.findByRaterRoleAndRaterId(RaterRole.PASSENGER, 100L);
        assertThat(result).isPresent();
        assertThat(result.get().getScore()).isEqualTo((byte) 5);
        assertThat(result.get().getComment()).isEqualTo("Comment updated");
    }
}
