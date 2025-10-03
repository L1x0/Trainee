package by.astakhau.trainee.driverservice.services;

import by.astakhau.trainee.driverservice.entities.Driver;
import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import by.astakhau.trainee.grpc.driver.DriverId;
import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverGrpcServiceTest {
    @InjectMocks
    private DriverGrpcService driverGrpcService;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private StreamObserver<GetDriverResponse> getDriverObserver;

    @Mock
    private StreamObserver<Empty> emptyObserver;

    @Captor
    private ArgumentCaptor<GetDriverResponse> getDriverResponseCaptor;

    @Captor
    private ArgumentCaptor<Driver> driverCaptor;


    @Test
    void getDriver() {
        Driver driver = new Driver();
        driver.setId(11L);
        driver.setName("Ivan");
        driver.setIsBusy(false);

        when(driverRepository.findFirstByIsBusy(false)).thenReturn(Optional.of(driver));

        driverGrpcService.getDriver(Empty.getDefaultInstance(), getDriverObserver);

        verify(driverRepository).findFirstByIsBusy(false);
        verify(driverRepository).saveAndFlush(driverCaptor.capture());
        Driver saved = driverCaptor.getValue();
        assertNotNull(saved);
        assertEquals(Boolean.TRUE, saved.getIsBusy(), "Driver must be marked as busy before saveAndFlush");

        verify(getDriverObserver).onNext(getDriverResponseCaptor.capture());
        GetDriverResponse resp = getDriverResponseCaptor.getValue();
        assertEquals(11L, resp.getDriverId());
        assertEquals("Ivan", resp.getName());

        verify(getDriverObserver).onCompleted();
        verifyNoMoreInteractions(getDriverObserver);
    }

    @Test
    void ridDriver() {
        long driverId = 123L;
        DriverId request = DriverId.newBuilder().setDriverId(driverId).build();

        driverGrpcService.ridDriver(request, emptyObserver);

        verify(driverRepository).ridDriver(driverId);

        verify(emptyObserver).onNext(Empty.getDefaultInstance());
        verify(emptyObserver).onCompleted();
        verifyNoMoreInteractions(emptyObserver);
    }
}
