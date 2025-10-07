package by.astakhau.trainee.ratingservice.cucumber;


import by.astakhau.trainee.ratingservice.dtos.TripResponseDto;
import by.astakhau.trainee.ratingservice.dtos.TripStatus;
import by.astakhau.trainee.ratingservice.integration.AbstractIntegrationTest;
import by.astakhau.trainee.ratingservice.repositories.RatingRepository;
import by.astakhau.trainee.ratingservice.services.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
public class RatingSteps extends AbstractIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @Given("the system is up")
    public void beforeScenario() {
        log.info("Before scenario");

        TripResponseDto responseDto = TripResponseDto.builder()
                .status(TripStatus.COMPLETED)
                .destinationAddress("Destination Address")
                .originAddress("Origin Address")
                .driverName("Driver Name")
                .passengerName("Passenger Name")
                .price(100)
                .build();


        AbstractIntegrationTest.WIREMOCK.stubFor(WireMock.post(WireMock.urlPathEqualTo("/rating/create"))
                .withQueryParam("id", WireMock.equalTo("100"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json").withResponseBody(null)));
    }

    @When("person creates request to create rating with comment {string} and score {string}")
    public void thePersonCreatesRatingWithCommentAndScore(String arg0, String arg1) {
        Map<String, Object> body = Map.of(
                "tripId", "100",
                "comment", arg0,
                "score", arg1,
                "ratingId", "20",
                "raterRole", "DRIVER"
        );

        ResponseEntity<Void> lastResponse = restTemplate.postForEntity("/rating/create", body, Void.class);
    }

    @Then("a rating record is saved to db with comment {string} and score {string}")
    public void aRatingRecordIsSavedToDbWithCommentAndScore(String arg0, String arg1) {
        ratingRepository.findById(100L);
    }
}
