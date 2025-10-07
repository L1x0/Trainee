package by.astakhau.trainee.driverservice.cucumber;

import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.integration.AbstractIntegrationTest;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import by.astakhau.trainee.driverservice.services.DriverService;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;
import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class DriverSteps {

    private Server server;
    private ManagedChannel channel;
    private DriverServiceGrpc.DriverServiceBlockingStub blockingStub;
    private TestDriverServiceImpl testService;
    private GetDriverResponse lastGetResponse;
    private Throwable lastException;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverService driverService;

    @Given("the system is up")
    public void theSystemIsUp() {
        String serverName = InProcessServerBuilder.generateName();

        testService = new TestDriverServiceImpl();

        try {
            server = InProcessServerBuilder.forName(serverName)
                    .directExecutor()
                    .addService(testService)
                    .build()
                    .start();

            channel = InProcessChannelBuilder.forName(serverName)
                    .directExecutor()
                    .build();

            blockingStub = DriverServiceGrpc.newBlockingStub(channel);

        } catch (IOException e) {
            log.error("Failed to start in-process gRPC server: {}", e.getMessage(), e);
            fail("Could not start in-process gRPC server: " + e.getMessage());
        }

        var driverRequestDto = DriverRequestDto.builder()
                .name("Artsiom")
                .email("artemasyahov27@gmail.com")
                .phoneNumber("+375447006485")
                .car(CarRequestDto.builder()
                        .make("mersedes")
                        .color("white")
                        .plateNumber("0000AA-5")
                        .build())
                .build();
        driverService.save(driverRequestDto);
    }

    @Given("test driver server will return driver with id {string} and name {string}")
    public void testDriverServerWillReturnDriverWithIdAndName(String idStr, String name) {
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid id: " + idStr);
        }
        testService.setNextDriver(id, name);

        lastGetResponse = null;
        lastException = null;
    }

    @When("client requests driver")
    public void clientRequestsDriver() {
        try {
            lastGetResponse = blockingStub.getDriver(Empty.getDefaultInstance());
            lastException = null;
        } catch (Throwable ex) {
            lastException = ex;
            lastGetResponse = null;
        }
    }

    @Then("response contains id {string} and name {string}")
    public void responseContainsIdAndName(String idStr, String name) {
        assertNull(lastException, "Expected no exception but got: " + lastException);
        assertNotNull(lastGetResponse, "Expected a response but got null");
        long expectedId = Long.parseLong(idStr);
        assertEquals(expectedId, lastGetResponse.getDriverId());
        assertEquals(name, lastGetResponse.getName());
    }

    @And("driver with id {string} become busy")
    public void driverWithIdBecomeBusy(String arg0) {
        var driver = driverRepository.findById(Long.valueOf(arg0));

        assertEquals(Boolean.TRUE, driver.get().getIsBusy());
    }

    public static class TestDriverServiceImpl extends DriverServiceGrpc.DriverServiceImplBase {
        @Setter
        private volatile boolean throwOnGet = false;
        private volatile Long nextDriverId = null;
        private volatile String nextName = null;
        @Getter
        private final List<Long> ridCalls = new CopyOnWriteArrayList<>();

        public void setNextDriver(long id, String name) {
            this.nextDriverId = id;
            this.nextName = name;
            this.throwOnGet = false;
        }

        @Override
        public void getDriver(Empty request, StreamObserver<GetDriverResponse> responseObserver) {
            if (throwOnGet) {
                responseObserver.onError(Status.UNAVAILABLE.withDescription("test induced error").asRuntimeException());
                return;
            }
            if (nextDriverId == null || nextName == null) {
                responseObserver.onError(Status.NOT_FOUND.withDescription("no test driver configured").asRuntimeException());
                return;
            }
            GetDriverResponse resp = GetDriverResponse.newBuilder()
                    .setDriverId(nextDriverId)
                    .setName(nextName)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }

        @Override
        public void ridDriver(by.astakhau.trainee.grpc.driver.DriverId request, StreamObserver<com.google.protobuf.Empty> responseObserver) {
            ridCalls.add(request.getDriverId());
            responseObserver.onNext(com.google.protobuf.Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }
    }
}
