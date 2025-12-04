package com.biancapasch.meetingroombooking2.controller;

import com.biancapasch.meetingroombooking2.domain.controller.BookingController;
import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.ApiExceptionHandler;
import com.biancapasch.meetingroombooking2.dtos.BookingRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.BookingResponseDTO;
import com.biancapasch.meetingroombooking2.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;
    private BookingService bookingService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookingService = Mockito.mock(BookingService.class);

        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        BookingController controller = new BookingController(bookingService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldCreateBookingAndReturn201() throws Exception {
        OffsetDateTime start = OffsetDateTime.now()
                .plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime end   = start.plusHours(1);

        BookingRequestDTO req = new BookingRequestDTO(
                99L,           // userId
                1L,            // meetingRoomCode
                101L,          // code
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        BookingResponseDTO resp = new BookingResponseDTO(
                99L,
                1L,
                101L,
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        Mockito.when(bookingService.createBooking(any(BookingRequestDTO.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(99))
                .andExpect(jsonPath("$.meetingRoomCode").value(1))
                .andExpect(jsonPath("$.code").value(101))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.numberOfPeople").value(4));
    }

    @Test
    void shouldReturn409WhenOverlap() throws Exception {
        OffsetDateTime start = OffsetDateTime.now()
                .plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime end   = start.plusHours(1);

        BookingRequestDTO req = new BookingRequestDTO(
                99L,
                1L,
                101L,
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        Mockito.when(bookingService.createBooking(any(BookingRequestDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Conflito de horário"));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Conflito")));
    }

    @Test
    void shouldReturnBookingList() throws Exception {
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(1);

        BookingResponseDTO b1 = new BookingResponseDTO(
                99L, 1L, 101L, BookingStatus.ACTIVE, 4, start, end
        );

        BookingResponseDTO b2 = new BookingResponseDTO(
                7L, 2L, 202L, BookingStatus.CANCELLED, 2, start.plusDays(1), end.plusDays(1)
        );

        Mockito.when(bookingService.getAllBookings())
                .thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(99))
                .andExpect(jsonPath("$[0].meetingRoomCode").value(1))
                .andExpect(jsonPath("$[1].userId").value(7))
                .andExpect(jsonPath("$[1].meetingRoomCode").value(2));
    }
}
