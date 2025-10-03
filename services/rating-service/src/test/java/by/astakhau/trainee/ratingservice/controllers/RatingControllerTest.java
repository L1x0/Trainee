package by.astakhau.trainee.ratingservice.controllers;

import by.astakhau.trainee.ratingservice.dtos.RatingRequestDto;
import by.astakhau.trainee.ratingservice.dtos.RatingResponseDto;
import by.astakhau.trainee.ratingservice.entities.RaterRole;
import by.astakhau.trainee.ratingservice.services.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RatingController.class)
public class RatingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RatingService ratingService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id").ascending());
        RatingResponseDto r1 = new RatingResponseDto();
        r1.setId(1L);
        RatingResponseDto r2 = new RatingResponseDto();
        r2.setId(2L);

        Page<RatingResponseDto> page = new PageImpl<>(List.of(r1, r2), pageable, 2);
        when(ratingService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/rating/all")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ratingService, times(1)).findAll(captor.capture());
        Pageable used = captor.getValue();

        assert used.getPageNumber() == 0;
        assert used.getPageSize() == 2;
    }

    @Test
    void getDriversReview() throws Exception {
        Pageable pageable = PageRequest.of(1, 5);
        RatingResponseDto r = new RatingResponseDto();
        r.setId(10L);
        Page<RatingResponseDto> page = new PageImpl<>(List.of(r), pageable, 1);

        when(ratingService.findByRaterRole(eq(RaterRole.DRIVER), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/rating/drivers-review")
                        .param("page", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(10));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ratingService).findByRaterRole(eq(RaterRole.DRIVER), captor.capture());
        Pageable used = captor.getValue();
        assert used.getPageNumber() == 1;
        assert used.getPageSize() == 5;
    }

    @Test
    void getPassengersReview() throws Exception {
        Pageable pageable = PageRequest.of(0, 3);
        RatingResponseDto r = new RatingResponseDto();
        r.setId(20L);
        Page<RatingResponseDto> page = new PageImpl<>(List.of(r), pageable, 1);

        when(ratingService.findByRaterRole(eq(RaterRole.PASSENGER), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/rating/passengers-review")
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(20));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ratingService).findByRaterRole(eq(RaterRole.PASSENGER), captor.capture());
        Pageable used = captor.getValue();
        assert used.getPageNumber() == 0;
        assert used.getPageSize() == 3;
    }

    @Test
    void createRating() throws Exception {
        RatingResponseDto ratingResponseDto = RatingResponseDto.builder()
                .id(1L)
                .raterRole(RaterRole.PASSENGER)
                .comment("comment")
                .score((byte) 3)
                .raterId(1L)
                .tripId(1L)
                .build();

        RatingRequestDto ratingRequestDto = RatingRequestDto.builder()
                .raterId(1L)
                .comment("comment")
                .score((byte) 3)
                .raterRole(RaterRole.PASSENGER)
                .tripId(1L)
                .build();

        when(ratingService.createRating(ratingRequestDto)).thenReturn(ratingResponseDto);

        mockMvc.perform(post("/rating/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk());

        verify(ratingService).createRating(ratingRequestDto);
    }

    @Test
    void updateRating() throws Exception {
        var ratingRequestDto = RatingRequestDto.builder()
                .raterId(1L)
                .score((byte) 3)
                .comment("comment")
                .tripId(1L)
                .raterRole(RaterRole.PASSENGER)
                .build();

        var ratingResponseDto = RatingResponseDto.builder()
                .id(1L)
                .raterId(1L)
                .score((byte) 3)
                .comment("comment")
                .tripId(1L)
                .raterRole(RaterRole.PASSENGER)
                .build();

        when(ratingService.update(1L, RaterRole.PASSENGER, ratingRequestDto)).thenReturn(ratingResponseDto);

        mockMvc.perform(put("/rating/update").param("raterId", "1").param("raterRole", "PASSENGER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comment").value("comment"))
                .andExpect(jsonPath("$.score").value(3))
                .andExpect(jsonPath("$.tripId").value(1));


        verify(ratingService).update(1L, RaterRole.PASSENGER, ratingRequestDto);
    }
}

