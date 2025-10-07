package by.astakhau.trainee.tripservice.cucumber;

import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import by.astakhau.trainee.tripservice.entities.Trip;
import by.astakhau.trainee.tripservice.grpc.DriverGrpcClient;
import by.astakhau.trainee.tripservice.repositories.TripRepository;
import by.astakhau.trainee.tripservice.integration.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TripSteps extends AbstractIntegrationTest {
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DriverGrpcClient driverGrpcClient;

    private String passengerName;
    private String originAddress;
    private String destinationAddress;
    private Long createdTripId;

    @Given("the system is up")
    public void the_system_is_up() throws Exception {
        // 1) чистим БД / WireMock
        tripRepository.deleteAll();

        createTopics(KAFKA.getBootstrapServers(), "trips.make", "trips.created");

        doReturn(GetDriverResponse.newBuilder()
                .setDriverId(1L)
                .setName("Tomas")
                .build())
                .when(driverGrpcClient).getDriver();
        doNothing().when(driverGrpcClient).ridDriver(anyLong());
    }

    @Given("write trip order to trips.make with passenger name {string} and passenger phone {string}")
    public void write_trip_order_to_topic(String passengerName, String passengerPhone) throws Exception {
        this.passengerName = passengerName;
        this.originAddress = "origin-test";
        this.destinationAddress = "destination-test";

        Map<String, Object> msg = Map.of(
                "passengerId", 1,
                "passengerName", passengerName,
                "passengerPhoneNumber", passengerPhone,
                "originAddress", originAddress,
                "destinationAddress", destinationAddress
        );

        Properties prodProps = new Properties();
        prodProps.put("bootstrap.servers", KAFKA.getBootstrapServers());
        prodProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prodProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // sync send to be deterministic in tests
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(prodProps)) {
            String value = objectMapper.writeValueAsString(msg);
            producer.send(new ProducerRecord<>("trips.make", null, value)).get(5, TimeUnit.SECONDS);
            producer.flush();
        }
    }

    @When("service make request for get free driver to grpc-server")
    public void service_make_request_for_get_free_driver_to_grpc_server() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> verify(driverGrpcClient, atLeastOnce()).getDriver());
    }

    @Then("trip save to db with driver info: name {string} and passenger phone {string}")
    public void trip_saved_to_db_with_driver_info(String expectedDriverName, String expectedPhone) {
        var found = tripRepository.findByOriginAddressAndDestinationAddressAndPassengerName(
                originAddress,
                destinationAddress,
                passengerName);

        assertThat(found).isPresent();
        Trip trip = found.get();
        assertThat(trip.getDriverName()).isEqualTo(expectedDriverName);

        this.createdTripId = trip.getId();
    }

    @Then("trip record with id is saved to kafka topic {string}")
    public void trip_record_with_id_is_saved_to_kafka_topic(String topic) throws Exception {
        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, "cucumber-consumer-" + UUID.randomUUID());
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps)) {
            consumer.subscribe(Collections.singletonList(topic));

            consumer.poll(java.time.Duration.ofSeconds(1));
            consumer.seekToBeginning(consumer.assignment());

            long deadline = System.currentTimeMillis() + 20000;
            boolean found = false;

            while (System.currentTimeMillis() < deadline && !found) {
                ConsumerRecords<String, String> recs = consumer.poll(java.time.Duration.ofSeconds(1));
                for (var record : recs) {
                    String val = record.value();
                    Map<?, ?> msg = objectMapper.readValue(val, Map.class);

                    if (Objects.equals(msg.get("originAddress"), originAddress)
                            && Objects.equals(msg.get("destinationAddress"), destinationAddress)
                            && Objects.equals(msg.get("passengerName"), passengerName)) {
                        assertThat(msg.get("id")).isNotNull();
                        assertThat(msg.get("driverName")).isEqualTo("Tomas");
                        found = true;
                        break;
                    }
                }
            }

            assertThat(found).as("Сообщение в %s не найдено за 20 секунд", topic).isTrue();
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
