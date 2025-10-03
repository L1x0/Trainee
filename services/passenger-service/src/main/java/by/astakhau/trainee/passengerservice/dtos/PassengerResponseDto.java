package by.astakhau.trainee.passengerservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
