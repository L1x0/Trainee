package by.astakhau.trainee.ratingservice.cucumber;

import by.astakhau.trainee.ratingservice.integration.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberConfiguration extends AbstractIntegrationTest {
}
