package by.astakhau.trainee.passengerservice.cucumber;

import by.astakhau.trainee.passengerservice.entities.Passenger;
import by.astakhau.trainee.passengerservice.integration.AbstractIntegrationTest;
import by.astakhau.trainee.passengerservice.repositories.PassengerRepository;
import by.astakhau.trainee.passengerservice.repositories.TripRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PassengerSteps extends AbstractIntegrationTest {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<Void> lastResponse;

    @Given("the system is up")
    public void beforeScenario() {
        log.info("Executing @BeforeScenario");

        passengerRepository.deleteAll();
        WIREMOCK.resetAll();
    }

    @Given("a passenger exists with name {string} and phone {string}")
    public void passenger_exists(String name, String phone) {
        log.info("Executing @Given");

        var p = new Passenger();

        p.setName(name);
        p.setPhoneNumber(phone);
        p.setEmail(name + "@example.com");
        p.setIsDeleted(false);
        passengerRepository.saveAndFlush(p);
    }

    @When("the passenger requests a trip from {string} to {string}")
    public void request_trip(String origin, String destination) {
        log.info("Executing @When");

        Map<String, Object> body = Map.of(
                "passengerId", "1",
                "passengerName", "ivan",
                "passengerPhoneNumber", "+375447006485",
                "originAddress", origin,
                "destinationAddress", destination
        );

        lastResponse = restTemplate.postForEntity("/passenger/create-order", body, Void.class);
    }

    @Then("a message should be published to topic {string} containing {string}")
    public void message_published(String topic, String expected) {
        log.info("Executing @Then");

        var props = new java.util.Properties();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + java.util.UUID.randomUUID());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class.getName());

        try (org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props)) {
            consumer.subscribe(java.util.Collections.singletonList(topic));
            org.apache.kafka.clients.consumer.ConsumerRecords<String, String> recs =
                    org.awaitility.Awaitility.await().atMost(java.time.Duration.ofSeconds(10))
                            .until(() -> {
                                var r = consumer.poll(java.time.Duration.ofSeconds(1));
                                return !r.isEmpty() ? r : null;
                            }, java.util.Objects::nonNull);

            var record = recs.iterator().next();

            try {
                writeRecordToCreatedTopic(record);
            } catch (JsonProcessingException e) {
                log.error("Error while writing record to topic", e);
            }

            assertThat(record.value()).contains(expected);
        }

    }

    @And("a message is saved to db containing record with id {string}")
    public void aMessageIsSavedToDbContaining(String arg0) {
        log.info("Executing @And");

        tripRepository.findById(Long.valueOf(arg0));

    }


    private void writeRecordToCreatedTopic(ConsumerRecord<String, String> rec) throws JsonProcessingException {
        var record = rec.value();
        Map<?, ?> msg = objectMapper.readValue(record, Map.class);

        Properties prodProps = new Properties();
        prodProps.put("bootstrap.servers", KAFKA.getBootstrapServers());
        prodProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prodProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Map<String, Object> map = Map.of(
                "passengerId", msg.get("passengerId"),
                "passengerName", msg.get("passengerName"),
                "passengerPhoneNumber", msg.get("passengerPhoneNumber"),
                "originAddress", msg.get("originAddress"),
                "destinationAddress", msg.get("destinationAddress"),
                "driverName", "driver name",
                "price", "1",
                "id", "1",
                "status", "COMPLETED"
        );

        String value = objectMapper.writeValueAsString(map);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(prodProps)) {
            producer.send(new org.apache.kafka.clients.producer.ProducerRecord<>("trips.created", null, value));
            producer.flush();
        }
    }
}
