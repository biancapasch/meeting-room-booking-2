package com.biancapasch.meetingroombooking2.controller;

import com.biancapasch.meetingroombooking2.domain.controller.UserController;
import com.biancapasch.meetingroombooking2.domain.exceptions.ApiExceptionHandler;
import com.biancapasch.meetingroombooking2.dtos.UserRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import com.biancapasch.meetingroombooking2.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        objectMapper = new ObjectMapper();

        UserController controller = new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception{
        UserRequestDTO req = new UserRequestDTO("Bianca", "biancapasch@email.com");

        UserResponseDTO response = new UserResponseDTO(1L,
                "Bianca",
                "biancapasch@email.com"
                );

        Mockito.when(userService.create(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bianca"))
                .andExpect(jsonPath("$.email").value("biancapasch@email.com"));
    }

    @Test
    void shouldReturn400WhenBodyIsInvalid() throws Exception {

        UserRequestDTO invalid = new UserRequestDTO(null, "   ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
