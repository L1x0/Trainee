package by.astakhau.trainee.passengerservice.dtos;

import lombok.Data;

@Data
public class PassengerRequestDto {
    private String name;
    private String email;
    private String phoneNumber;
}
