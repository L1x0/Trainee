package by.astakhau.trainee.tripservice.controllers;

import by.astakhau.trainee.tripservice.dtos.TripResponseDto;
import by.astakhau.trainee.tripservice.entities.TripStatus;
import by.astakhau.trainee.tripservice.services.TripService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
public class TripControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TripService tripService;

    @Test
    void getAllTrips_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        TripResponseDto t1 = TripResponseDto.builder().id(1L).build();
        TripResponseDto t2 = TripResponseDto.builder().id(2L).build();
        Page<TripResponseDto> page = new PageImpl<>(List.of(t1, t2), pageable, 2);

        when(tripService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/trips/all")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(tripService).findAll(any(Pageable.class));
    }

    @Test
    void getActiveTrips_returnsAcceptedStatusPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        TripResponseDto t = TripResponseDto.builder().id(5L).status(TripStatus.ACCEPTED).build();
        Page<TripResponseDto> page = new PageImpl<>(List.of(t), pageable, 1);

        when(tripService.findAllByStatus(any(Pageable.class), eq(TripStatus.ACCEPTED))).thenReturn(page);

        mockMvc.perform(get("/trips/accepted")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(5));

        verify(tripService).findAllByStatus(any(Pageable.class), eq(TripStatus.ACCEPTED));
    }

    @Test
    void getTripById_found_returns200WithBody() throws Exception {
        TripResponseDto dto = TripResponseDto.builder().id(11L).build();
        when(tripService.findById(11L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/trips")
                        .param("id", "11")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11));

        verify(tripService).findById(11L);
    }

    @Test
    void getTripById_notFound_returns404() throws Exception {
        when(tripService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/trips")
                        .param("id", "999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tripService).findById(999L);
    }

    @Test
    void deleteTrip_callsServiceAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/trips")
                        .param("destinationAddress", "addr")
                        .param("driverName", "Ivan"))
                .andExpect(status().isNoContent());

        verify(tripService).delete("Ivan", "addr");
    }

    @Test
    void acceptTrip_callsChangeStatus_andReturnsNoContent() throws Exception {
        mockMvc.perform(put("/trips/accept").param("id", "1"))
                .andExpect(status().isNoContent());

        verify(tripService).changeStatus(1L, TripStatus.ACCEPTED);
    }

    @Test
    void canselTrip_callsChangeStatus_andReturnsNoContent() throws Exception {
        mockMvc.perform(put("/trips/cansel").param("id", "2"))
                .andExpect(status().isNoContent());

        verify(tripService).changeStatus(2L, TripStatus.CANCELLED);
    }

    @Test
    void pickUpTrip_callsChangeStatus_andReturnsNoContent() throws Exception {
        mockMvc.perform(put("/trips/en-route-to-pickup").param("id", "3"))
                .andExpect(status().isNoContent());

        verify(tripService).changeStatus(3L, TripStatus.EN_ROUTE_TO_PICKUP);
    }

    @Test
    void destinationTrip_callsChangeStatus_andReturnsNoContent() throws Exception {
        mockMvc.perform(put("/trips/en-route-to-destination").param("id", "4"))
                .andExpect(status().isNoContent());

        verify(tripService).changeStatus(4L, TripStatus.EN_ROUTE_TO_DESTINATION);
    }

    @Test
    void changeStatus_complete_callsEndOfTrip_andReturnsNoContent() throws Exception {
        mockMvc.perform(put("/trips/complete").param("id", "5"))
                .andExpect(status().isNoContent());

        verify(tripService).endOfTrip(5L);
    }
}
