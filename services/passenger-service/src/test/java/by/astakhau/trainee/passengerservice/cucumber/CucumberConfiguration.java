package by.astakhau.trainee.passengerservice.cucumber;

import by.astakhau.trainee.passengerservice.integration.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberConfiguration extends AbstractIntegrationTest {
}
