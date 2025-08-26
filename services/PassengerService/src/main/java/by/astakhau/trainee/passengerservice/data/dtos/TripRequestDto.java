package by.astakhau.trainee.passengerservice.data.dtos;

import lombok.Data;

@Data
public class TripRequestDto {
    private String name;
    private String phoneNumber;
    private String destinationAddress;
    private String originAddress;
}
