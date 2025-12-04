package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.BookingEntity;
import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.InactiveMeetingRoomException;
import com.biancapasch.meetingroombooking2.dtos.BookingRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.BookingResponseDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.mapper.BookingMapper;
import com.biancapasch.meetingroombooking2.mapper.MeetingRoomMapper;
import com.biancapasch.meetingroombooking2.repository.BookingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final MeetingRoomService meetingRoomService;
    private final UserService userService;
    private final BookingMapper mapper;

    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        if (!request.start().isBefore(request.end())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "startTime deve ser anterior a endTime");
        }

        MeetingRoomEntity meetingRoomEntity = meetingRoomService.findByCode(request.meetingRoomCode());

        if (meetingRoomEntity.getStatus() == MeetingRoomStatus.INACTIVE) {
            throw new InactiveMeetingRoomException("Sala não pode estar inativa");
        }

        UserEntity userEntity = userService.findById(request.userId());

        boolean conflict = repository.existsOverlap(
                meetingRoomEntity.getId(), request.start(), request.end()
        );
        if (conflict) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Conflito de horário: já existe reserva ativa que se sobrepõe ao intervalo solicitado (regra [start,end))."
            );
        }

        BookingEntity booking = mapper.toEntity(request);

        booking.setMeetingRoom(meetingRoomEntity);
        booking.setUserEntity(userEntity);

        BookingEntity saved = repository.save(booking);

        return mapper.toResponse(saved);
    }

    public List<BookingResponseDTO> getAllBookings() {
        List<BookingEntity> bookings = repository.findAll();

        return bookings.stream()
                .map(mapper::toResponse)
                .toList();
    }

}
