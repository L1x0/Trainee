package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.entities.Passenger;
import by.astakhau.trainee.passengerservice.mappers.PassengerMapper;
import by.astakhau.trainee.passengerservice.repositories.PassengerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerService {

    final private PassengerRepository passengerRepository;
    final private PassengerMapper passengerMapper;

    @Transactional
    public void savePassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerMapper.fromRequestDto(passengerRequestDto);

        passenger.setDeletedAt(null);
        passenger.setIsDeleted(false);

        passengerRepository.save(passenger);

        log.info("Passenger saved with ID: {}, phone number: {}, email: {}",
                passenger.getId(),  passenger.getPhoneNumber(), passenger.getEmail());
    }

    public PassengerResponseDto findById(Long id) {
        return passengerMapper.passengerToPassengerResponseDto(passengerRepository.findById(id).orElse(null));
    }

    public Page<PassengerResponseDto> findAll(Pageable pageable) {
        var results = passengerRepository.findAll(pageable);

        return results.map(passengerMapper::passengerToPassengerResponseDto);
    }

    public Page<PassengerResponseDto> findAllByName(String name,  Pageable pageable) {
        var results = passengerRepository.findByName(name, pageable);

        log.info("Passengers found with name: {}, pageable: {}", name, pageable);

        return results.map(passengerMapper::passengerToPassengerResponseDto);
    }


    @Transactional
    public void deleteWithEmail(String name, String email) {
        passengerRepository.softDeleteByNameAndEmail(name, email);

        log.info("Passengers deleted with name: {}, email: {}", name, email);
    }

    @Transactional
    public void createTripOrder(TripRequestDto tripRequestDto) {
        var owner = getOrderOwner(tripRequestDto);

        if(owner.isEmpty())
            throw new IllegalArgumentException("There isn't people with same info");
        else {
            //отправить данные на сервис поездок
        }
    }

    private Optional<Passenger> getOrderOwner(TripRequestDto tripRequestDto) {
        log.info("Getting owner for trip request: {}", tripRequestDto.toString());

        return passengerRepository.findByNameAndPhoneNumber(
                tripRequestDto.getName(),
                tripRequestDto.getPhoneNumber());
    }
}
