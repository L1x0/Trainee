package by.astakhau.trainee.driverservice.controllers;


import by.astakhau.trainee.driverservice.dtos.CarRequestDto;
import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.dtos.DriverRequestDto;
import by.astakhau.trainee.driverservice.dtos.DriverResponseDto;
import by.astakhau.trainee.driverservice.entities.Car;
import by.astakhau.trainee.driverservice.entities.Driver;
import by.astakhau.trainee.driverservice.services.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(DriverController.class)
public class DriverControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DriverService driverService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDriver() throws Exception {
        var carRequestDto = CarRequestDto.builder()
                .color("blue")
                .make("X5")
                .plateNumber("0000AA-5")
                .build();

        var requestDto = DriverRequestDto.builder()
                .email("artemastahov27@gmail.com")
                .name("name")
                .car(carRequestDto)
                .phoneNumber("+375447006485")
                .build();

        var driver = Driver.builder()
                .email("email")
                .name("name")
                .id(1L)
                .phoneNumber("phoneNumber")
                .car(new Car())
                .createdAt(null)
                .deletedAt(null)
                .isBusy(false)
                .isDeleted(false)
                .version(0)
                .build();

        var response = DriverResponseDto.builder()
                .id(1L)
                .email("artemastahov27@gmail.com")
                .name("name")
                .phoneNumber("+375447006485")
                .car(new CarResponseDto())
                .isBusy(false)
                .isDeleted(false)
                .build();

        when(driverService.save(requestDto)).thenReturn(response);

        mockMvc.perform(post("/drivers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/drivers?id=1")));

        verify(driverService, times(1)).save(requestDto);
    }

    @Test
    void updateDriver() throws Exception {
        var carRequestDto = CarRequestDto.builder()
                .color("blue")
                .make("X5")
                .plateNumber("0000AA-5")
                .build();

        var requestDto = DriverRequestDto.builder()
                .email("artemastahov27@gmail.com")
                .name("name")
                .car(carRequestDto)
                .phoneNumber("+375447006485")
                .build();


        var response = DriverResponseDto.builder()
                .id(1L)
                .email("artemastahov27@gmail.com")
                .name("name")
                .phoneNumber("+375447006485")
                .car(new CarResponseDto())
                .isBusy(false)
                .isDeleted(false)
                .build();

        when(driverService.update(requestDto)).thenReturn(response);

        mockMvc.perform(put("/drivers/update").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("artemastahov27@gmail.com"));

    }

    @Test
    void getAllDrivers_returnsPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);
        DriverResponseDto d1 = DriverResponseDto.builder().id(1L).name("Ivan").build();
        DriverResponseDto d2 = DriverResponseDto.builder().id(2L).name("Petr").build();

        Page<DriverResponseDto> page = new PageImpl<>(List.of(d1, d2), pageable, 2);
        when(driverService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/drivers")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Ivan"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(driverService).findAll(captor.capture());
        Pageable used = captor.getValue();
        assert used.getPageNumber() == 0;
        assert used.getPageSize() == 2;
    }

    @Test
    void getAllDrivers_emptyPage_returnsEmptyContent() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DriverResponseDto> empty = Page.empty(pageable);
        when(driverService.findAll(any(Pageable.class))).thenReturn(empty);

        mockMvc.perform(get("/drivers")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(driverService).findAll(any(Pageable.class));
    }

    @Test
    void getDriverById_found_returns200AndBody() throws Exception {
        DriverResponseDto dto = DriverResponseDto.builder().id(11L).name("Ivan").build();
        when(driverService.findById(11L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/drivers")
                        .param("id", "11")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Ivan"));

        verify(driverService).findById(11L);
    }

    @Test
    void getDriverById_notFound_returns404() throws Exception {
        when(driverService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/drivers")
                        .param("id", "999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(driverService).findById(999L);
    }

    @Test
    void deleteDriver_callsService_andReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/drivers")
                        .param("name", "Ivan")
                        .param("email", "ivan@mail"))
                .andExpect(status().isNoContent());

        verify(driverService).deleteByNameAndEmail("Ivan", "ivan@mail");
        verifyNoMoreInteractions(driverService);
    }

    @Test
    void deleteDriver_withoutParams_callsServiceWithNulls_andReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/drivers"))
                .andExpect(status().isNoContent());

        verify(driverService).deleteByNameAndEmail(null, null);
    }
}
