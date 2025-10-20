package by.astakhau.trainee.passengerservice.mappers;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;
import org.springframework.stereotype.Component;

@Component
public class PassengerMapperImpl implements PassengerMapper {

    @Override
    public PassengerResponseDto passengerToPassengerResponseDto(Passenger passenger) {
        return PassengerResponseDto.builder()
                .id(passenger.getId())
                .uuid(passenger.getUuid())
                .name(passenger.getName())
                .phoneNumber(passenger.getPhoneNumber())
                .email(passenger.getEmail())
                .build();
    }

    @Override
    public Passenger fromRequestDto(PassengerRequestDto passengerRequestDto) {
        return Passenger.builder()
                .deletedAt(null)
                .name(passengerRequestDto.getName())
                .phoneNumber(passengerRequestDto.getPhoneNumber())
                .email(passengerRequestDto.getEmail())
                .isDeleted(false)
                .version(0)
                .id(null)
                .build();
    }
}
