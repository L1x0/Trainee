package by.astakhau.trainee.tripservice.services;

import by.astakhau.trainee.tripservice.data.dtos.TripRequestDto;
import by.astakhau.trainee.tripservice.data.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.data.entities.TripStatus;
import by.astakhau.trainee.tripservice.data.mappers.TripMapper;
import by.astakhau.trainee.tripservice.data.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    public TripResponseDto findById(Long id) {
        return tripMapper.toTripResponseDto(tripRepository.findById(id).orElse(null));
    }

    public List<TripResponseDto> findAll() {
        var tempResult = tripRepository.findAll();

        return tempResult.stream().map(tripMapper::toTripResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public void save(TripRequestDto trip) {
        /* Думаю, тут стоит прикрутить данные с других сервисов */
    }

    @Transactional
    public void changeStatus(TripRequestDto tripRequestDto, TripStatus status) { // мб лучше сделать по схеме 1 функция = 1 статус
        var trip = tripRepository.findTripByRequestInfo(
                tripRequestDto.getOriginAddress(),
                tripRequestDto.getDestinationAddress(),
                tripRequestDto.getPassengerName());

        if (trip.isPresent()) {
            trip.get().setStatus(status);
            tripRepository.save(trip.get());
        }
    }

    @Transactional
    public void delete(TripRequestDto tripRequestDto) {
        var trip = tripRepository.findTripByRequestInfo(
                tripRequestDto.getOriginAddress(),
                tripRequestDto.getDestinationAddress(),
                tripRequestDto.getPassengerName());

        trip.ifPresent(tripRepository::delete);
    }
}
