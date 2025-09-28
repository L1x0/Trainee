package by.astakhau.trainee.tripservice.kafka;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.services.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripsOrdersConsumer {
    private final KafkaTemplate<String, TripRequestDto> kafkaTemplate;
    private final TripService tripService;
    private final CreatedTripsProducer createdTripsProducer;

    @KafkaListener(topics = "trips.make")
    public void listen(ConsumerRecord<String, TripRequestDto> record, Consumer<?, ?> consumer) {
        TripRequestDto dto = record.value();

        var groupId = consumer.groupMetadata();

        kafkaTemplate.executeInTransaction(kt -> {
            TopicPartition tp = new TopicPartition(record.topic(), record.partition());
            OffsetAndMetadata oam = new OffsetAndMetadata(record.offset() + 1);
            Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(tp, oam);

            kt.sendOffsetsToTransaction(offsets, groupId);

            log.info("Received trip request: {}", dto);

            var tripResponse = tripService.createTrip(dto);

            createdTripsProducer.send(tripResponse);

            return null;
        });
    }
}
