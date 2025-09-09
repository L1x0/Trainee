package by.astakhau.trainee.passengerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "by.astakhau.trainee.passengerservice.client")
public class PassengerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassengerServiceApplication.class, args);
    }

}
