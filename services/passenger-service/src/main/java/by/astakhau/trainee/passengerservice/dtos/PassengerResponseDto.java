package by.astakhau.trainee.passengerservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
