package by.astakhau.trainee.passengerservice.integration;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.services.PassengerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Collections.singletonList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class PassengerKafkaIntegrationIT extends AbstractIntegrationTest {

    private static final String TOPIC_TRIPS_MAKE = "trips.make";
    private static final String TOPIC_TRIPS_CREATED = "trips.created";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ExecutorService responderExecutor;
    private AtomicBoolean responderRunning;

    @BeforeAll
    void beforeAll() throws Exception {
        createTopics(KAFKA.getBootstrapServers(), TOPIC_TRIPS_MAKE, TOPIC_TRIPS_CREATED);
        truncateTopic(KAFKA.getBootstrapServers(), TOPIC_TRIPS_MAKE);
        truncateTopic(KAFKA.getBootstrapServers(), TOPIC_TRIPS_CREATED);

        responderRunning = new AtomicBoolean(true);
        responderExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "trip-responder");
            t.setDaemon(true);
            return t;
        });
        responderExecutor.submit(this::tripResponderLoop);

        passengerService.savePassenger(PassengerRequestDto.builder()
                .name("KafkaTestUser")
                .phoneNumber("+375447006385")
                .email("artemastahov27@gmail.com")
                .build());
    }

    @AfterAll
    void afterAll() {
        responderRunning.set(false);
        if (responderExecutor != null) responderExecutor.shutdownNow();
    }

    @Test
    void createOrder_and_updatesDb() throws Exception {
        Pageable pageable = PageRequest.of(0, 1);
        PassengerResponseDto p = passengerService.findAllByName("KafkaTestUser", pageable).iterator().next();

        Map<String, Object> req = new HashMap<>();
        req.put("passengerId", p.getId());
        req.put("passengerName", "KafkaTestUser");
        req.put("passengerPhoneNumber", "+375447006385");
        req.put("originAddress", "From A");
        req.put("destinationAddress", "To B");

        var resp = restTemplate.postForEntity("/passenger/create-order", req, Void.class);
        assertThat(resp.getStatusCode().value()).isIn(204);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-checker-" + UUID.randomUUID());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(singletonList(TOPIC_TRIPS_MAKE));
            ConsumerRecords<String, String> recs = consumer.poll(Duration.ofSeconds(5));

            assertThat(recs.count()).isGreaterThanOrEqualTo(1);

            var record = recs.iterator().next();
            String val = record.value();
            Map<?, ?> msg = objectMapper.readValue(val, Map.class);

            assertThat(msg.get("passengerName")).isEqualTo("KafkaTestUser");
            assertThat(msg.get("destinationAddress")).isEqualTo("To B");
        }

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(singletonList(TOPIC_TRIPS_CREATED));
            ConsumerRecords<String, String> recs = consumer.poll(Duration.ofSeconds(5));

            assertThat(recs.count()).isGreaterThanOrEqualTo(1);
            var record = recs.iterator().next();
            String val = record.value();
            Map<?, ?> msg = objectMapper.readValue(val, Map.class);

            assertThat(msg.containsKey("tripId")).isTrue();
        }


        boolean ok = false;

        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            Integer cnt = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM passengers WHERE name = ? AND phone_number = ?",
                    Integer.class, "KafkaTestUser", "+375447006385");
            if (cnt != null && cnt > 0) {
                ok = true;
                break;
            }
        }

        assertThat(ok).isTrue();
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

    public static void truncateTopic(String bootstrapServers, String topic) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient admin = AdminClient.create(props)) {
            TopicDescription desc = admin.describeTopics(Collections.singletonList(topic))
                    .all().get().get(topic);

            List<TopicPartition> partitions = desc.partitions().stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toList());

            if (partitions.isEmpty()) return;

            Map<TopicPartition, OffsetSpec> request = new HashMap<>();
            for (TopicPartition tp : partitions) {
                request.put(tp, OffsetSpec.latest());
            }

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> offsets =
                    admin.listOffsets(request).all().get();

            Map<TopicPartition, RecordsToDelete> toDelete = new HashMap<>();
            for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> e : offsets.entrySet()) {
                long latest = e.getValue().offset();
                toDelete.put(e.getKey(), RecordsToDelete.beforeOffset(latest));
            }

            admin.deleteRecords(toDelete).all().get();
            Thread.sleep(200);
        }
    }


    private void tripResponderLoop() {
        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, "trip-responder-" + UUID.randomUUID());
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Properties prodProps = new Properties();
        prodProps.put("bootstrap.servers", KAFKA.getBootstrapServers());
        prodProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prodProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer =
                     new org.apache.kafka.clients.consumer.KafkaConsumer<>(consProps);
             org.apache.kafka.clients.producer.KafkaProducer<String, String> producer =
                     new org.apache.kafka.clients.producer.KafkaProducer<>(prodProps)) {

            consumer.subscribe(singletonList(TOPIC_TRIPS_MAKE));

            while (responderRunning.get()) {
                ConsumerRecords<String, String> recs = consumer.poll(Duration.ofMillis(500));
                recs.forEach(r -> {
                    try {
                        Map<?, ?> request = objectMapper.readValue(r.value(), Map.class);

                        Map<String, Object> response = new HashMap<>();
                        response.put("tripId", "resp-" + UUID.randomUUID());
                        response.put("passengerName", request.get("passengerName"));
                        response.put("destinationAddress", request.get("destinationAddress"));

                        String value = objectMapper.writeValueAsString(response);
                        producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>(TOPIC_TRIPS_CREATED, null, value));
                        producer.flush();
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        log.error("Error sending trip response", e);
                    }
                });
            }
        } catch (KafkaException ex) {
            log.error("kafka problem");
        }
    }
}
