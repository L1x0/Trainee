package by.astakhau.trainee.passengerservice.dtos;

import lombok.Data;

@Data
public class TripRequestDto {
    private long id;
    private String name;
    private String phoneNumber;
    private String destinationAddress;
    private String originAddress;
}
