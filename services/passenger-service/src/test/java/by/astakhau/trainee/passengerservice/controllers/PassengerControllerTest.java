package by.astakhau.trainee.passengerservice.controllers;

import by.astakhau.trainee.passengerservice.controller.PassengerController;
import by.astakhau.trainee.passengerservice.dtos.PassengerRequestDto;
import by.astakhau.trainee.passengerservice.dtos.PassengerResponseDto;
import by.astakhau.trainee.passengerservice.dtos.TripRequestDto;
import by.astakhau.trainee.passengerservice.dtos.TripResponseDto;
import by.astakhau.trainee.passengerservice.services.PassengerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PassengerController.class)
public class PassengerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PassengerService passengerService;

    @Test
    void createPassengerTest() throws Exception {
        PassengerRequestDto passengerRequestDto = PassengerRequestDto.builder()
                .name("Artsiom")
                .email("artemastahov27@gmail.com")
                .phoneNumber("+375447006485")
                .build();

        PassengerResponseDto passengerResponseDto = PassengerResponseDto.builder().id(1L).build();

        when(passengerService.savePassenger(any(PassengerRequestDto.class))).thenReturn(passengerResponseDto);

        mockMvc.perform(post("/passenger/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/passenger?id=1")));

        verify(passengerService).savePassenger(argThat(d -> "Artsiom".equals(d.getName())
                && "+375447006485".equals(d.getPhoneNumber())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(passengerService.findById(1L)).thenReturn(Optional.of(PassengerResponseDto.builder().id(1L).build()));

        mockMvc.perform(get("/passenger").param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(passengerService).findById(1L);
    }

    @Test
    void getAllTest() throws Exception {
        PassengerResponseDto p1 = new PassengerResponseDto();
        p1.setId(1L);
        p1.setName("A");
        PassengerResponseDto p2 = new PassengerResponseDto();
        p2.setId(2L);
        p2.setName("B");

        Pageable pageable = PageRequest.of(0, 2);
        Page<PassengerResponseDto> page = new PageImpl<>(List.of(p1, p2), pageable, 2);

        when(passengerService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/passenger/all").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));

        verify(passengerService).findAll(argThat(p -> p.getPageSize() == 2 && p.getPageNumber() == 0));
    }

    @Test
    void updateTest() throws Exception {
        var requestDto = PassengerRequestDto.builder()
                .name("Artsiom")
                .phoneNumber("+375447006499")
                .email("artemastahov27@Gmail.ru")
                .build();

        when(passengerService.update("artsiom", "+375447006485", requestDto))
                .thenReturn(PassengerResponseDto.builder()
                        .id(1L)
                        .phoneNumber("+375447006499")
                        .email("artemastahov27@Gmail.ru")
                        .build());

        mockMvc.perform(put("/passenger/update")
                        .param("name", "artsiom")
                        .param("phoneNumber", "+375447006485")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.phoneNumber").value("+375447006499"));
    }

    @Test
    void createTripTest() throws Exception {
        TripRequestDto tripRequestDto = TripRequestDto.builder()
                .passengerId(1L)
                .passengerName("Artsiom")
                .originAddress("место отправления")
                .destinationAddress("Место назначения")
                .passengerPhoneNumber("+375447006485")
                .build();

        mockMvc.perform(post("/passenger/create-order")
                        .content(objectMapper.writeValueAsString(tripRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(passengerService).createTripOrder(tripRequestDto);
    }

    @Test
    void search_byName_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        PassengerResponseDto p1 = new PassengerResponseDto(); p1.setId(1L);
        PassengerResponseDto p2 = new PassengerResponseDto(); p2.setId(2L);
        Page<PassengerResponseDto> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(passengerService.findAllByName(eq("Ivan"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/passenger")
                        .param("name", "Ivan")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(passengerService).findAllByName(eq("Ivan"), captor.capture());
        Pageable used = captor.getValue();
        assert used.getPageNumber() == 0;
        assert used.getPageSize() == 2;
    }

    @Test
    void deleteByEmail_callsService_andReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/passenger/delete")
                        .param("name", "Ivan")
                        .param("email", "ivan@mail"))
                .andExpect(status().isNoContent());

        verify(passengerService).deleteWithEmail("Ivan", "ivan@mail");
        verifyNoMoreInteractions(passengerService);
    }

    @Test
    void deleteByEmail_withoutParams_callsServiceWithNulls() throws Exception {
        mockMvc.perform(delete("/passenger/delete")
                        .param("email", "x@y.z"))
                .andExpect(status().isNoContent());

        verify(passengerService).deleteWithEmail(null, "x@y.z");
    }

    @Test
    void createOrder_validDto_callsService_andReturnsNoContent() throws Exception {
        TripRequestDto req = TripRequestDto.builder()
                .passengerId(1L)
                .passengerName("Ivan")
                .passengerPhoneNumber("+375447006485")
                .originAddress("Aadsfb")
                .destinationAddress("Badfadf")
                .build();

        doNothing().when(passengerService).createTripOrder(any(TripRequestDto.class));

        mockMvc.perform(post("/passenger/create-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(passengerService).createTripOrder(argThat(dto ->
                dto.getPassengerId() == 1L
                        && "Ivan".equals(dto.getPassengerName())
        ));
    }

    @Test
    void getTripInfo_present_returns200WithBody() throws Exception {
        TripResponseDto resp = TripResponseDto.builder()
                .id(1L)
                .build();
        when(passengerService.getTripInfo("Ivan")).thenReturn(Optional.of(resp));

        mockMvc.perform(get("/passenger/get-trip-info")
                        .param("passengerName", "Ivan")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(passengerService).getTripInfo("Ivan");
    }

    @Test
    void getTripInfo_absent_returns404() throws Exception {
        when(passengerService.getTripInfo("Nobody")).thenReturn(Optional.empty());

        mockMvc.perform(get("/passenger/get-trip-info")
                        .param("passengerName", "Nobody"))
                .andExpect(status().isNotFound());

        verify(passengerService).getTripInfo("Nobody");
    }
}
