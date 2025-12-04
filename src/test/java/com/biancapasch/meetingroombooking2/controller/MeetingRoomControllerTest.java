package com.biancapasch.meetingroombooking2.controller;

import com.biancapasch.meetingroombooking2.domain.controller.MeetingRoomController;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.ApiExceptionHandler;
import com.biancapasch.meetingroombooking2.domain.exceptions.MeetingRoomNotFoundException;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import com.biancapasch.meetingroombooking2.service.MeetingRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MeetingRoomControllerTest {

    private MockMvc mockMvc;
    private MeetingRoomService meetingRoomService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        meetingRoomService = Mockito.mock(MeetingRoomService.class);

        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MeetingRoomController controller = new MeetingRoomController(meetingRoomService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldCreateMeetingRoomSuccessfully() throws Exception {
        MeetingRoomRequestDTO req = new MeetingRoomRequestDTO(
                101L,
                "Sala de reuniões",
                40,
                MeetingRoomStatus.ACTIVE
        );

        MeetingRoomResponseDTO resp = new MeetingRoomResponseDTO(
                101L,
                "Sala de reuniões",
                40,
                MeetingRoomStatus.ACTIVE
        );

        Mockito.when(meetingRoomService.createMeetingRoom(any(MeetingRoomRequestDTO.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/meeting-rooms")   // <<<<<< AQUI COM BARRA
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(101))
                .andExpect(jsonPath("$.name").value("Sala de reuniões"))
                .andExpect(jsonPath("$.capacity").value(40))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldFindMeetingRoomByCode() throws Exception {
        Long code = 101L;

        MeetingRoomResponseDTO resp = new MeetingRoomResponseDTO(
                code,
                "Sala de reuniões",
                40,
                MeetingRoomStatus.ACTIVE
        );

        Mockito.when(meetingRoomService.findByCodeAndReturnDTO(eq(code)))
                .thenReturn(resp);

        mockMvc.perform(get("/meeting-rooms/{code}", code))  // <<<<<< AQUI COM BARRA
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(101))
                .andExpect(jsonPath("$.name").value("Sala de reuniões"))
                .andExpect(jsonPath("$.capacity").value(40))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn400WhenBodyIsInvalid() throws Exception {
        MeetingRoomRequestDTO invalid = new MeetingRoomRequestDTO(
                null,
                "   ",
                -1,
                MeetingRoomStatus.ACTIVE
        );

        mockMvc.perform(post("/meeting-rooms")  // <<<<<< AQUI COM BARRA
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenRoomNotFound() throws Exception {
        Long code = 999L;

        Mockito.when(meetingRoomService.findByCodeAndReturnDTO(eq(code)))
                .thenThrow(new MeetingRoomNotFoundException("Sala 999 não encontrada"));

        mockMvc.perform(get("/meeting-rooms/{code}", code))  // <<<<<< AQUI COM BARRA
                .andExpect(status().isNotFound());
    }
}
