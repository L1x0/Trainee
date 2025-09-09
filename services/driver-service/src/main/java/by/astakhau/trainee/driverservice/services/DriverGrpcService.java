package by.astakhau.trainee.driverservice.services;


import by.astakhau.trainee.driverservice.repositories.DriverRepository;
import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;
import by.astakhau.trainee.grpc.driver.GetDriverResponse;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class DriverGrpcService extends DriverServiceGrpc.DriverServiceImplBase {
    private final DriverRepository driverRepository;

    @Override
    public void getDriver(Empty request, StreamObserver<GetDriverResponse> responseObserver) {
        var driver = driverRepository.findFirstByIsBusy(false);

        if (driver.isPresent()) {

            GetDriverResponse resp = GetDriverResponse.newBuilder()
                    .setDriverId(driver.get().getId())
                    .setName(driver.get().getName())
                    .build();

            driver.get().setIsBusy(true);
            driverRepository.saveAndFlush(driver.get());

            responseObserver.onNext(resp);
            responseObserver.onCompleted();

            log.info("GetDriverResponse success.");
            log.info("Saved driver: {}", driver.get());
        } else {
            throw new IllegalStateException("System has not not busy drivers");
        }
    }

    @Override
    @Transactional
    public void ridDriver(by.astakhau.trainee.grpc.driver.DriverId request,
                          StreamObserver<com.google.protobuf.Empty> responseObserver) {
        driverRepository.ridDriver(request.getDriverId());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
