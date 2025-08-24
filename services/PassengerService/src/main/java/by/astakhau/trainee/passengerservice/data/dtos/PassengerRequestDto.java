package by.astakhau.trainee.passengerservice.data.dtos;

import lombok.Data;

@Data
public class PassengerRequestDto {
    private String name;
    private String email;
    private String phoneNumber;
}
