package by.astakhau.trainee.passengerservice.dtos;

import lombok.Data;

@Data
public class PassengerResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
