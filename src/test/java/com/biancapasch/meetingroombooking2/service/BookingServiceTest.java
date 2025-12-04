package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.BookingEntity;
import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.InactiveMeetingRoomException;
import com.biancapasch.meetingroombooking2.dtos.BookingRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.BookingResponseDTO;
import com.biancapasch.meetingroombooking2.mapper.BookingMapper;
import com.biancapasch.meetingroombooking2.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MeetingRoomService meetingRoomService;

    @Mock
    private UserService userService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private static UserEntity user(Long id) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setName("User " + id);
        u.setEmail("user" + id + "@mail.com");
        return u;
    }

    private static MeetingRoomEntity room(Long id, Long code, MeetingRoomStatus status) {
        MeetingRoomEntity r = new MeetingRoomEntity();
        r.setId(id);
        r.setCode(code);
        r.setName("Sala " + code);
        r.setCapacity(10);
        r.setStatus(status);
        return r;
    }

    // ========= TESTE FELIZ (happy path) =========

    @Test
    void shouldCreateBookingSuccessfullyAndReturnDTO() {
        // given
        OffsetDateTime start = OffsetDateTime.now()
                .plusDays(1)
                .withSecond(0)
                .withNano(0);
        OffsetDateTime end = start.plusHours(1);

        BookingRequestDTO request = new BookingRequestDTO(
                5L,                       // userId
                101L,                     // meetingRoomCode
                999L,                     // code da reserva
                BookingStatus.ACTIVE,     // status
                4,                        // numberOfPeople
                start,
                end
        );

        UserEntity user = user(5L);
        MeetingRoomEntity meetingRoom = room(1L, 101L, MeetingRoomStatus.ACTIVE);

        BookingEntity mapped = new BookingEntity();
        mapped.setCode(request.code());
        mapped.setStatus(request.status());
        mapped.setNumberOfPeople(request.numberOfPeople());
        mapped.setStartTime(request.start());
        mapped.setEndTime(request.end());

        BookingEntity saved = new BookingEntity();
        saved.setId(42L);
        saved.setUserEntity(user);
        saved.setMeetingRoom(meetingRoom);
        saved.setCode(request.code());
        saved.setStatus(BookingStatus.ACTIVE);
        saved.setNumberOfPeople(request.numberOfPeople());
        saved.setStartTime(start);
        saved.setEndTime(end);

        BookingResponseDTO responseDTO = new BookingResponseDTO(
                user.getId(),
                meetingRoom.getCode(),
                saved.getCode(),
                saved.getStatus(),
                saved.getNumberOfPeople(),
                saved.getStartTime(),
                saved.getEndTime()
        );

        when(meetingRoomService.findByCode(101L)).thenReturn(meetingRoom);
        when(userService.findById(5L)).thenReturn(user);
        when(bookingRepository.existsOverlap(1L, start, end)).thenReturn(false);

        when(bookingMapper.toEntity(request)).thenReturn(mapped);
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(saved);
        when(bookingMapper.toResponse(saved)).thenReturn(responseDTO);

        // when
        BookingResponseDTO dto = bookingService.createBooking(request);

        // then – verifica DTO de resposta
        assertThat(dto.userId()).isEqualTo(5L);
        assertThat(dto.meetingRoomCode()).isEqualTo(101L);
        assertThat(dto.code()).isEqualTo(999L);
        assertThat(dto.status()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(dto.numberOfPeople()).isEqualTo(4);
        assertThat(dto.start()).isEqualTo(start);
        assertThat(dto.end()).isEqualTo(end);

        // captura o BookingEntity que foi salvo
        ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
        verify(bookingRepository).save(captor.capture());
        BookingEntity toSave = captor.getValue();

        // garante que o service setou user e meetingRoom corretos
        assertThat(toSave.getUserEntity()).isSameAs(user);
        assertThat(toSave.getMeetingRoom()).isSameAs(meetingRoom);
        assertThat(toSave.getStatus()).isEqualTo(BookingStatus.ACTIVE);

        verify(meetingRoomService, times(1)).findByCode(101L);
        verify(userService, times(1)).findById(5L);
        verify(bookingRepository, times(1)).existsOverlap(1L, start, end);
        verify(bookingMapper, times(1)).toEntity(request);
        verify(bookingMapper, times(1)).toResponse(saved);
    }

    // ========= VALIDAÇÃO start < end =========

    @Test
    void shouldThrowBAD_REQUESTWhenEndNotAfterStart() {
        // given
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start; // igual → inválido

        BookingRequestDTO request = new BookingRequestDTO(
                5L,
                101L,
                999L,
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        // when
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class,
                        () -> bookingService.createBooking(request));

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(meetingRoomService);
        verifyNoInteractions(userService);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    // ========= SALA INATIVA =========

    @Test
    void shouldThrowInactiveMeetingRoomWhenMeetingRoomIsInactive() {
        // given
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(1);

        BookingRequestDTO request = new BookingRequestDTO(
                5L,
                101L,
                999L,
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        MeetingRoomEntity inactiveRoom = room(1L, 101L, MeetingRoomStatus.INACTIVE);

        when(meetingRoomService.findByCode(101L)).thenReturn(inactiveRoom);

        // when
        InactiveMeetingRoomException ex =
                assertThrows(InactiveMeetingRoomException.class,
                        () -> bookingService.createBooking(request));

        // then
        assertThat(ex.getMessage()).contains("Sala não pode estar inativa");

        verify(meetingRoomService, times(1)).findByCode(101L);
        verifyNoInteractions(userService);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    // ========= CONFLITO DE HORÁRIO =========

    @Test
    void shouldThrow409ConflictWhenOverlappingBooking() {
        // given
        OffsetDateTime start = OffsetDateTime.now().plusDays(1);
        OffsetDateTime end = start.plusHours(1);

        BookingRequestDTO request = new BookingRequestDTO(
                5L,
                101L,
                999L,
                BookingStatus.ACTIVE,
                4,
                start,
                end
        );

        MeetingRoomEntity room = room(1L, 101L, MeetingRoomStatus.ACTIVE);
        UserEntity user = user(5L);

        when(meetingRoomService.findByCode(101L)).thenReturn(room);
        when(userService.findById(5L)).thenReturn(user);
        when(bookingRepository.existsOverlap(1L, start, end)).thenReturn(true);

        // when
        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class,
                        () -> bookingService.createBooking(request));

        // then
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getReason()).contains("Conflito de horário");

        verify(bookingRepository, never()).save(any());
        verify(bookingMapper, never()).toEntity(any());
    }

    // ========= LISTAR TODOS =========

    @Test
    void getAllBookingsShouldReturnMappedList() {
        // given
        UserEntity user1 = user(1L);
        UserEntity user2 = user(2L);
        MeetingRoomEntity room1 = room(10L, 101L, MeetingRoomStatus.ACTIVE);
        MeetingRoomEntity room2 = room(11L, 202L, MeetingRoomStatus.ACTIVE);

        BookingEntity b1 = new BookingEntity();
        b1.setId(100L);
        b1.setUserEntity(user1);
        b1.setMeetingRoom(room1);
        b1.setCode(111L);
        b1.setStatus(BookingStatus.ACTIVE);
        b1.setNumberOfPeople(3);
        b1.setStartTime(OffsetDateTime.now().plusDays(1));
        b1.setEndTime(b1.getStartTime().plusHours(1));

        BookingEntity b2 = new BookingEntity();
        b2.setId(200L);
        b2.setUserEntity(user2);
        b2.setMeetingRoom(room2);
        b2.setCode(222L);
        b2.setStatus(BookingStatus.CANCELLED);
        b2.setNumberOfPeople(5);
        b2.setStartTime(OffsetDateTime.now().plusDays(2));
        b2.setEndTime(b2.getStartTime().plusHours(2));

        List<BookingEntity> entities = List.of(b1, b2);

        BookingResponseDTO dto1 = new BookingResponseDTO(
                user1.getId(),
                room1.getCode(),
                b1.getCode(),
                b1.getStatus(),
                b1.getNumberOfPeople(),
                b1.getStartTime(),
                b1.getEndTime()
        );

        BookingResponseDTO dto2 = new BookingResponseDTO(
                user2.getId(),
                room2.getCode(),
                b2.getCode(),
                b2.getStatus(),
                b2.getNumberOfPeople(),
                b2.getStartTime(),
                b2.getEndTime()
        );

        when(bookingRepository.findAll()).thenReturn(entities);
        when(bookingMapper.toResponse(b1)).thenReturn(dto1);
        when(bookingMapper.toResponse(b2)).thenReturn(dto2);

        // when
        List<BookingResponseDTO> result = bookingService.getAllBookings();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).userId()).isEqualTo(1L);
        assertThat(result.get(0).meetingRoomCode()).isEqualTo(101L);
        assertThat(result.get(1).userId()).isEqualTo(2L);
        assertThat(result.get(1).meetingRoomCode()).isEqualTo(202L);

        verify(bookingRepository, times(1)).findAll();
        verify(bookingMapper, times(1)).toResponse(b1);
        verify(bookingMapper, times(1)).toResponse(b2);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }
}
