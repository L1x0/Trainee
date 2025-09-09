package by.astakhau.trainee.tripservice.config;


import by.astakhau.trainee.grpc.driver.DriverServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    private final GrpcChannelFactory channelFactory;

    public GrpcClientConfig(GrpcChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    @Bean
    public ManagedChannel driverManagedChannel() {
        return channelFactory.createChannel("driver");
    }

    @Bean
    public DriverServiceGrpc.DriverServiceBlockingStub driverBlockingStub(ManagedChannel driverManagedChannel) {
        return DriverServiceGrpc.newBlockingStub(driverManagedChannel);
    }
}