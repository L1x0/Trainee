package by.astakhau.trainee.driverservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@org.testcontainers.junit.jupiter.Testcontainers
public abstract class AbstractIntegrationTest {

    @org.testcontainers.junit.jupiter.Container
    @org.springframework.boot.testcontainers.service.connection.ServiceConnection
    static final org.testcontainers.containers.PostgreSQLContainer<?> POSTGRES =
            new org.testcontainers.containers.PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("drivers_db")
                    .withUsername("drivers_user")
                    .withPassword("dev");

    static final com.github.tomakehurst.wiremock.WireMockServer WIREMOCK =
            new com.github.tomakehurst.wiremock.WireMockServer(
                    com.github.tomakehurst.wiremock.core.WireMockConfiguration.options().dynamicPort());

    static {
        WIREMOCK.start();
    }
}
