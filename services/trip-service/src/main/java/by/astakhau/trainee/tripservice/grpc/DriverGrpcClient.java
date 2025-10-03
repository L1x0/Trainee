package by.astakhau.trainee.tripservice.grpc;

import by.astakhau.trainee.grpc.driver.DriverId;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;

import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverGrpcClient {
    private final DriverServiceGrpc.DriverServiceBlockingStub driverStub;

    @CircuitBreaker(name = "driverService", fallbackMethod = "getDriverFallback")
    public GetDriverResponse getDriver() {
        log.info("Driver stub info: {}", driverStub.getChannel().toString());
        return driverStub.getDriver(Empty.getDefaultInstance());
    }

    @CircuitBreaker(name = "driverService", fallbackMethod = "ridDriverFallback")
    public void ridDriver(long driverId) {
        log.info("Trying to rid driver: {}", driverId);
        driverStub.ridDriver(DriverId.newBuilder().setDriverId(driverId).build());
    }

    public GetDriverResponse getDriverFallback(Throwable ex) {
        log.warn("driverService fallback for getDriver — returning 503. cause: {}: {}",
                ex.getClass().getSimpleName(), ex.getMessage(), ex);

        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Driver service unavailable", ex);
    }

    public void ridDriverFallback(long driverId, Throwable ex) {
        log.warn("driverService fallback for ridDriver({}) — failing with 503. cause: {}: {}",
                driverId, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Driver service unavailable, try again later", ex);
    }
}
