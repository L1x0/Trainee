package by.astakhau.trainee.passengerservice.kafka;

import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.services.TripInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatedTripsConsumer {
    private final KafkaTemplate<String, TripResponseDto> kafkaTemplate;
    private final TripInfoService tripService;

    @KafkaListener(topics = "trips.created")
    public void listen(ConsumerRecord<String, TripResponseDto> record, Consumer<?, ?> consumer) {
        var groupId = consumer.groupMetadata();

        log.info("Received record: {}", record);

        kafkaTemplate.executeInTransaction(kt -> {
            tripService.saveOrUpdateTripInfo(record.value());

            TopicPartition tp = new TopicPartition(record.topic(), record.partition());
            OffsetAndMetadata oam = new OffsetAndMetadata(record.offset() + 1);
            Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(tp, oam);

            kt.sendOffsetsToTransaction(offsets, groupId);

            return null;
        });


    }
}
