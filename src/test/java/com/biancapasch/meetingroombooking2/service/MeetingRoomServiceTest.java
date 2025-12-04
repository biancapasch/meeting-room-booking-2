package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.MeetingRoomNotFoundException;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import com.biancapasch.meetingroombooking2.mapper.MeetingRoomMapper;
import com.biancapasch.meetingroombooking2.repository.MeetingRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepository meetingRoomRepository;

    @Mock
    private MeetingRoomMapper meetingRoomMapper;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    @Test
    void shouldCreateMeetingRoomSuccessfully() {
        // given
        MeetingRoomRequestDTO dto = new MeetingRoomRequestDTO(
                1L,
                "Sala de estudos",
                88,
                null // status vindo nulo para o service setar como ACTIVE
        );

        MeetingRoomEntity toPersist = new MeetingRoomEntity(
                null,
                dto.code(),
                dto.name(),
                dto.capacity(),
                null,
                null,
                null
        );

        MeetingRoomEntity saved = new MeetingRoomEntity(
                23L,
                dto.code(),
                dto.name(),
                dto.capacity(),
                MeetingRoomStatus.ACTIVE,
                OffsetDateTime.now(),
                null
        );

        MeetingRoomResponseDTO responseDTO = new MeetingRoomResponseDTO(
                saved.getCode(),
                saved.getName(),
                saved.getCapacity(),
                saved.getStatus()
        );

        when(meetingRoomMapper.toEntity(dto)).thenReturn(toPersist);
        when(meetingRoomRepository.save(any(MeetingRoomEntity.class))).thenReturn(saved);
        when(meetingRoomMapper.toResponse(saved)).thenReturn(responseDTO);

        ArgumentCaptor<MeetingRoomEntity> captor = ArgumentCaptor.forClass(MeetingRoomEntity.class);

        // when
        MeetingRoomResponseDTO response = meetingRoomService.createMeetingRoom(dto);

        // then
        verify(meetingRoomRepository, times(1)).save(captor.capture());
        MeetingRoomEntity captured = captor.getValue();

        // verifica dados que foram montados antes do save
        assertThat(captured.getCode()).isEqualTo(dto.code());
        assertThat(captured.getName()).isEqualTo(dto.name());
        assertThat(captured.getCapacity()).isEqualTo(dto.capacity());
        // service garante status ACTIVE se vier nulo
        assertThat(captured.getStatus()).isEqualTo(MeetingRoomStatus.ACTIVE);

        // verifica o DTO de resposta
        assertThat(response.code()).isEqualTo(saved.getCode());
        assertThat(response.name()).isEqualTo(saved.getName());
        assertThat(response.capacity()).isEqualTo(saved.getCapacity());
        assertThat(response.status()).isEqualTo(saved.getStatus());
    }

    @Test
    void shouldFindMeetingRoomByCodeSuccessfully() {
        // given
        Long code = 101L;

        MeetingRoomEntity entity = new MeetingRoomEntity(
                23L,
                code,
                "Sala de estudos",
                88,
                MeetingRoomStatus.ACTIVE,
                OffsetDateTime.now(),
                null
        );

        when(meetingRoomRepository.findByCode(code)).thenReturn(Optional.of(entity));

        // when
        MeetingRoomEntity result = meetingRoomService.findByCode(code);

        // then
        assertThat(result).isSameAs(entity);
        verify(meetingRoomRepository, times(1)).findByCode(code);
        verifyNoMoreInteractions(meetingRoomRepository);
    }

    @Test
    void findByCodeShouldThrowMeetingRoomNotFoundWhenCodeDoesNotExist() {
        // given
        Long code = 999L;
        when(meetingRoomRepository.findByCode(code)).thenReturn(Optional.empty());

        // when
        MeetingRoomNotFoundException ex =
                assertThrows(MeetingRoomNotFoundException.class,
                        () -> meetingRoomService.findByCode(code));

        // then
        assertThat(ex.getMessage()).contains("MeetingRoom com " + code + " não encontrado");
        verify(meetingRoomRepository, times(1)).findByCode(code);
        verifyNoMoreInteractions(meetingRoomRepository);
    }

    @Test
    void shouldReturnMeetingRoomByCodeAsDTO() {
        // given
        Long code = 101L;

        MeetingRoomEntity entity = new MeetingRoomEntity(
                23L,
                code,
                "Sala de estudos",
                88,
                MeetingRoomStatus.ACTIVE,
                OffsetDateTime.now(),
                null
        );

        MeetingRoomResponseDTO dto = new MeetingRoomResponseDTO(
                entity.getCode(),
                entity.getName(),
                entity.getCapacity(),
                entity.getStatus()
        );

        when(meetingRoomRepository.findByCode(code)).thenReturn(Optional.of(entity));
        when(meetingRoomMapper.toResponse(entity)).thenReturn(dto);

        // when
        MeetingRoomResponseDTO response = meetingRoomService.findByCodeAndReturnDTO(code);

        // then
        assertThat(response.code()).isEqualTo(code);
        assertThat(response.name()).isEqualTo("Sala de estudos");
        assertThat(response.capacity()).isEqualTo(88);
        assertThat(response.status()).isEqualTo(MeetingRoomStatus.ACTIVE);

        verify(meetingRoomRepository, times(1)).findByCode(code);
        verify(meetingRoomMapper, times(1)).toResponse(entity);
        verifyNoMoreInteractions(meetingRoomRepository, meetingRoomMapper);
    }
}
