package by.astakhau.trainee.driverservice.cucumber;


import by.astakhau.trainee.driverservice.integration.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberConfiguration extends AbstractIntegrationTest {
}
