package by.astakhau.trainee.tripservice.kafka;

import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreatedTripsProducer {
    private final KafkaTemplate<String, TripResponseDto> kafkaTemplate;

    public void send(TripResponseDto tripResponse) {
        log.info("Sending trip request to kafka-broker: {}", tripResponse);

        log.info("Sending trip request to kafka-broker: {}", tripResponse);

        kafkaTemplate.send("trips.created", tripResponse);
    }
}
