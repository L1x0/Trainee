package by.astakhau.trainee.tripservice.cucumber;

import by.astakhau.trainee.tripservice.integration.AbstractIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberConfiguration extends AbstractIntegrationTest {
    @org.springframework.boot.test.mock.mockito.MockBean
    private by.astakhau.trainee.tripservice.grpc.DriverGrpcClient driverGrpcClient;

    // опцией: если хотите гарантированно не создавать реальный канал/стаб:
    @org.springframework.boot.test.mock.mockito.MockBean
    private by.astakhau.trainee.grpc.driver.DriverServiceGrpc.DriverServiceBlockingStub driverStub;
}
