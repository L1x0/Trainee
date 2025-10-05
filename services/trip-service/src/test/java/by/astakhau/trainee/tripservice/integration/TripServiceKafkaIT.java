package by.astakhau.trainee.tripservice.integration;

import by.astakhau.trainee.tripservice.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.grpc.DriverGrpcClient;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import by.astakhau.trainee.tripservice.services.TripService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.*;

import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class TripServiceKafkaIT extends AbstractIntegrationTest {
    private static final String TOPIC_TRIPS_MAKE = "trips.make";
    private static final String TOPIC_TRIPS_CREATED = "trips.created";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TripService tripService;

    @Autowired
    private TripRepository tripRepository;

    @MockitoBean
    DriverGrpcClient driverGrpcClient;


    private Properties consProps;

    private TripRequestDto tripRequestDto;

    @BeforeAll
    void beforeAll() throws Exception {
        createTopics(KAFKA.getBootstrapServers(), TOPIC_TRIPS_MAKE, TOPIC_TRIPS_CREATED);

        consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, "trip-responder-" + UUID.randomUUID());
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        tripRequestDto = TripRequestDto.builder()
                .PassengerId(1L)
                .destinationAddress("Destination address")
                .originAddress("Origin address")
                .passengerName("Passenger name")
                .build();

    }

    @Test
    void KafkaTripOrderProcess() throws Exception {
        tripRepository.save(Trip.builder()
                .id(1L)
                .destinationAddress("Destination address")
                .price(7)
                .orderDateTime(OffsetDateTime.now())
                .originAddress("Origin address")
                .passengerName("passenger")
                .createdAt(OffsetDateTime.now())
                .driverId(1L)
                .driverName("driver")
                .isDeleted(false)
                .status(TripStatus.COMPLETED)
                .passengerId(1L)
                .build());

        doNothing().when(driverGrpcClient).ridDriver(any(Long.class));
        tripService.endOfTrip(1L);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps)) {
            consumer.subscribe(singletonList(TOPIC_TRIPS_CREATED));
            ConsumerRecords<String, String> recs = consumer.poll(Duration.ofSeconds(5));

            assertThat(recs.count()).isGreaterThanOrEqualTo(1);

            var record = recs.iterator().next();
            String val = record.value();
            Map<?, ?> msg = objectMapper.readValue(val, Map.class);

            assertThat(msg.get("id")).isEqualTo(1);
            assertThat(msg.get("destinationAddress")).isEqualTo("Destination address");
        }
    }

    private static void createTopics(String bootstrapServers, String... topics) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        try (AdminClient admin = AdminClient.create(props)) {

            Set<String> existing = admin.listTopics().names().get(5, TimeUnit.SECONDS);

            List<NewTopic> toCreate = Arrays.stream(topics)
                    .filter(t -> !existing.contains(t))
                    .map(t -> new NewTopic(t, 1, (short) 1))
                    .collect(Collectors.toList());

            if (toCreate.isEmpty()) {
                return;
            }

            try {
                admin.createTopics(toCreate).all().get(10, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof org.apache.kafka.common.errors.TopicExistsException) {
                    System.out.println("Some topics already exist, ignoring TopicExistsException");
                } else {
                    throw e;
                }
            } catch (InterruptedException | TimeoutException ex) {
                throw ex;
            }
        }
    }
}
