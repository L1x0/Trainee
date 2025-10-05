package by.astakhau.trainee.tripservice.integration;

import by.astakhau.trainee.grpc.driver.DriverId;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;
import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import by.astakhau.trainee.tripservice.grpc.DriverGrpcClient;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Import(TripServiceGrpcIT.GrpcTestConfig.class)
public class TripServiceGrpcIT extends AbstractIntegrationTest{

    @Autowired
    private DriverGrpcClient client;

    @Autowired
    private TestDriverServiceImpl testDriverService;

    @Test
    void getDriver_successfulResponse_isForwardedToClient() {
        testDriverService.setNextDriver(42L, "Ivan");

        GetDriverResponse resp = client.getDriver();

        assertThat(resp).isNotNull();
        assertEquals(42L, resp.getDriverId());
        assertEquals("Ivan", resp.getName());
    }

    @Test
    void ridDriver_serverReceivesCall_andRecordsId() {
        long id = 777L;

        client.ridDriver(id);

        assertThat(testDriverService.getRidCalls()).contains(id);
    }

    @Test
    void getDriver_whenServerReturnsError_triggersFallback_thatThrows503() {
        testDriverService.setThrowOnGet(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.getDriver());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void ridDriver_whenServerReturnsError_triggersFallback_thatThrows503() {
        testDriverService.setThrowOnRid(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> client.ridDriver(100L));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @TestConfiguration
    static class GrpcTestConfig {
        private final String serverName = InProcessServerBuilder.generateName();

        @Bean
        public TestDriverServiceImpl testDriverService() {
            return new TestDriverServiceImpl();
        }

        @Bean(destroyMethod = "shutdownNow")
        public Server inProcessServer(TestDriverServiceImpl impl) throws Exception {
            Server server = InProcessServerBuilder.forName(serverName)
                    .directExecutor()
                    .addService(impl)
                    .build()
                    .start();
            return server;
        }

        @Bean(destroyMethod = "shutdownNow")
        public ManagedChannel managedChannel() {
            return InProcessChannelBuilder.forName(serverName)
                    .directExecutor()
                    .build();
        }

        @Bean
        public DriverServiceGrpc.DriverServiceBlockingStub driverBlockingStub(ManagedChannel managedChannel) {
            return DriverServiceGrpc.newBlockingStub(managedChannel);
        }
    }

    public static class TestDriverServiceImpl extends DriverServiceGrpc.DriverServiceImplBase {
        @Getter
        private final List<Long> ridCalls = new CopyOnWriteArrayList<>();
        @Setter
        private volatile boolean throwOnGet = false;
        @Setter
        private volatile boolean throwOnRid = false;
        private volatile Long nextDriverId = null;
        private volatile String nextName = null;

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
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
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
        public void ridDriver(DriverId request, StreamObserver<Empty> responseObserver) {
            if (throwOnRid) {
                responseObserver.onError(Status.UNAVAILABLE.withDescription("test induced error").asRuntimeException());
                return;
            }
            ridCalls.add(request.getDriverId());
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }
    }
}
