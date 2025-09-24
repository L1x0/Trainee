package by.astakhau.trainee.passengerservice.kafka;

import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    public KafkaProducer(@Autowired KafkaTemplate<String, TripRequestDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private final KafkaTemplate<String, TripRequestDto> kafkaTemplate;

    public void sendTripRequest(TripRequestDto tripRequest) {
        log.info("Sending trip request to kafka-broker: {}", tripRequest);

        log.info("Sending trip request to kafka-broker: {}", tripRequest);

        kafkaTemplate.executeInTransaction(kt -> {
            kt.send("trips.make", tripRequest);
            return null;
        });
    }
}
