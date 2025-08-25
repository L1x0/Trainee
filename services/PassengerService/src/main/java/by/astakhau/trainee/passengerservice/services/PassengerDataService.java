package by.astakhau.trainee.passengerservice.services;

import by.astakhau.trainee.passengerservice.data.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.data.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.data.entities.Passenger;
import by.astakhau.trainee.passengerservice.data.mappers.PassengerMapper;
import by.astakhau.trainee.passengerservice.data.repositories.PassengerRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerDataService {

    final private PassengerRepository passengerRepository;
    final private PassengerMapper passengerMapper;

    @Transactional
    public void savePassenger(PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerMapper.fromRequestDto(passengerRequestDto);

        passenger.setDeletedAt(null);
        passenger.setIsDeleted(false);

        passengerRepository.save(passenger);
    }

    @Transactional(readOnly = true)
    public PassengerResponseDto findById(Long id) {
        return passengerMapper.fromPassenger(passengerRepository.findById(id).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<PassengerResponseDto> findAll() {
        var results = passengerRepository.findAll();
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();

        results.forEach(passenger -> {
            passengerResponseDtos.add(passengerMapper.fromPassenger(passenger));
        });

        return passengerResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<PassengerResponseDto> findAllByName(String name) {
        var results = passengerRepository.findByName(name);
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();

        results.forEach(passenger -> {
            passengerResponseDtos.add(passengerMapper.fromPassenger(passenger));
        });

        return passengerResponseDtos;
    }

    @Transactional
    public List<PassengerResponseDto> findAllByIsDeleted(boolean isDeleted) {
        var results = passengerRepository.findByIsDeleted(isDeleted);
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();

        results.forEach(passenger -> {
            passengerResponseDtos.add(passengerMapper.fromPassenger(passenger));
        });

        return passengerResponseDtos;
    }

    @Transactional
    public void deleteById(long id) {
        passengerRepository.deleteById(id);
    }

    @Transactional
    public void delete(@NonNull PassengerRequestDto passengerRequestDto) {
        Passenger passenger = passengerMapper.fromRequestDto(passengerRequestDto);

        passenger.setDeletedAt(null);
        passenger.setIsDeleted(false);

        passengerRepository.delete(passenger);
    }
}
