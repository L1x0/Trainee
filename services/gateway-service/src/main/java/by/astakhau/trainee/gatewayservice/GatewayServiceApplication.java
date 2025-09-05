package by.astakhau.trainee.gatewayservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        var routeLocator = builder.routes()

                .route("driver-route", r -> r
                        .path( "/drivers/**")
                        .uri("lb://driver-service")
                )

                .route("passenger-route", r -> r
                        .path("/passenger", "/passenger/**")
                        .uri("lb://passenger-service")
                )

                .route("trip-route", r -> r
                        .path("/trips", "/trips/**")
                        .uri("lb://trip-service")
                )

                .route("rating-route", r -> r
                        .path("/rating", "/rating/**")
                        .uri("lb://rating-service")
                )
                .build();

        routeLocator.getRoutes().subscribe(route ->
                log.info("Loaded route: id={}, uri={}, predicate={}", route.getId(), route.getUri(), route.getPredicate())
        );

        return routeLocator;
    }
}
