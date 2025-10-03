package by.astakhau.trainee.driverservice.controllers;

import by.astakhau.trainee.driverservice.dtos.CarResponseDto;
import by.astakhau.trainee.driverservice.services.CarService;
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
import java.util.Optional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CarService carService;



    @Test
    void getAll_returnsPage() throws Exception {

        Pageable pageable = PageRequest.of(0, 2);
        CarResponseDto a = CarResponseDto.builder().plateNumber("ABC1").build();
        CarResponseDto b = CarResponseDto.builder().plateNumber("ABC2").build();

        Page<CarResponseDto> page = new PageImpl<>(List.of(a, b), pageable, 2);
        when(carService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/cars/all")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].plateNumber").value("ABC1"))
                .andExpect(jsonPath("$.totalElements").value(2));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(carService).findAll(captor.capture());
        Pageable used = captor.getValue();
        assert used.getPageNumber() == 0;
        assert used.getPageSize() == 2;
    }

    @Test
    void findById_found_returnsOkWithBody() throws Exception {
        CarResponseDto dto = CarResponseDto.builder()
                .plateNumber("XYZ10")
                .build();

        when(carService.findById(10L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/cars")
                        .param("id", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.plateNumber").value("XYZ10"));

        verify(carService).findById(10L);
    }

    @Test
    void findById_notFound_returns404() throws Exception {
        when(carService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/cars")
                        .param("id", "999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(carService).findById(999L);
    }
}
