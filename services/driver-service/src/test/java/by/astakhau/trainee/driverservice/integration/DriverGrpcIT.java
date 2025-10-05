package by.astakhau.trainee.driverservice.integration;

import by.astakhau.trainee.driverservice.entities.Driver;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import by.astakhau.trainee.driverservice.services.DriverGrpcService;
import by.astakhau.trainee.grpc.driver.DriverId;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;
import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverGrpcIT {

    private Server server;
    private ManagedChannel channel;
    private DriverServiceGrpc.DriverServiceBlockingStub blockingStub;

    @Mock
    private DriverRepository driverRepository;

    @BeforeEach
    void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        DriverGrpcService service = new DriverGrpcService(driverRepository);

        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(service)
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();

        blockingStub = DriverServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void getDriver() {
        Driver driver = mock(Driver.class);

        when(driver.getId()).thenReturn(42L);
        when(driver.getName()).thenReturn("Ivan");
        when(driverRepository.findFirstByIsBusy(false)).thenReturn(Optional.of(driver));

        GetDriverResponse resp = blockingStub.getDriver(Empty.getDefaultInstance());

        assertNotNull(resp);
        assertEquals(42L, resp.getDriverId());
        assertEquals("Ivan", resp.getName());

        verify(driver).setIsBusy(true);
        verify(driverRepository).saveAndFlush(driver);
        verify(driverRepository).findFirstByIsBusy(false);
    }

    @Test
    void getDriver_throwsStatusRuntimeException() {
        when(driverRepository.findFirstByIsBusy(false)).thenReturn(Optional.empty());

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.getDriver(Empty.getDefaultInstance()));

        assertNotNull(ex.getStatus());
    }

    @Test
    void ridDriver() {
        long driverId = 123L;

        doNothing().when(driverRepository).ridDriver(driverId);

        DriverId req = DriverId.newBuilder().setDriverId(driverId).build();
        blockingStub.ridDriver(req);

        verify(driverRepository, times(1)).ridDriver(driverId);
    }

    @Test
    void getDriver_marksCorrectEntityBusy_argumentCaptorDemo() {
        Driver driver = mock(Driver.class);

        when(driver.getId()).thenReturn(7L);
        when(driver.getName()).thenReturn("Petr");
        when(driverRepository.findFirstByIsBusy(false)).thenReturn(Optional.of(driver));

        GetDriverResponse resp = blockingStub.getDriver(Empty.getDefaultInstance());

        assertEquals(7L, resp.getDriverId());
        assertEquals("Petr", resp.getName());

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepository).saveAndFlush(captor.capture());

        Driver saved = captor.getValue();

        assertSame(driver, saved);

        verify(saved).setIsBusy(true);
    }
}
