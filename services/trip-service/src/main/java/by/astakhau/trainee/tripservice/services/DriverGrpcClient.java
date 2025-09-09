package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.grpc.driver.DriverId;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;

import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverGrpcClient {
    private final DriverServiceGrpc.DriverServiceBlockingStub driverStub;

    public GetDriverResponse getDriver() {
        log.info("Driver stub info: {}", driverStub.getChannel().toString());
        return driverStub.getDriver(Empty.getDefaultInstance());
    }

    public void ridDriver(long driverId) {
        log.info("Trying to rid driver: {}", driverId);
        driverStub.ridDriver(DriverId.newBuilder().setDriverId(driverId).build());
    }
}
