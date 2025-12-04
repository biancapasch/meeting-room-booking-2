package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.domain.exceptions.UserNotFoundException;
import com.biancapasch.meetingroombooking2.dtos.UserRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import com.biancapasch.meetingroombooking2.mapper.UserMapper;
import com.biancapasch.meetingroombooking2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userMapper = Mockito.mock(UserMapper.class);

        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserRequestDTO dto = new UserRequestDTO("bianca", "bianca@me.com");

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        UserEntity saved = new UserEntity();
        saved.setId(1L);
        saved.setCreatedAt(OffsetDateTime.now());
        saved.setEmail("bianca@me.com");
        saved.setName("bianca");

        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        UserResponseDTO response = userService.create(dto);

        verify(userRepository, times(1)).save(captor.capture());
        UserEntity toSave = captor.getValue();

        assertThat(toSave.getName()).isEqualTo(dto.name());
        assertThat(toSave.getEmail()).isEqualTo(dto.email());

        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.name()).isEqualTo(saved.getName());
        assertThat(response.email()).isEqualTo(saved.getEmail());
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        Long id = 1L;

        UserEntity userEntity = new UserEntity(1L, "Bianca", "bianca@email.com", OffsetDateTime.now(), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(userEntity);

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findByIdShouldThrowUserNotFoundWhenIdDoesNotExist() {
        Long id = 2L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.findById(id));

        assertThat(ex.getMessage()).contains("Usuário com id  " + id + " não encontrado");

        verify(userRepository, times(1)).findById(id);
    }

}
