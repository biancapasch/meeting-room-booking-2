package com.biancapasch.meetingroombooking2.mapper;

import com.biancapasch.meetingroombooking2.domain.entity.BookingEntity;
import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.dtos.BookingRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.BookingResponseDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import com.biancapasch.meetingroombooking2.repository.BookingRepository;
import com.biancapasch.meetingroombooking2.repository.MeetingRoomRepository;
import com.biancapasch.meetingroombooking2.repository.UserRepository;
import com.biancapasch.meetingroombooking2.service.MeetingRoomService;
import com.biancapasch.meetingroombooking2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public BookingEntity toEntity(BookingRequestDTO bookingRequestDTO) {
        BookingEntity booking = new BookingEntity();

        booking.setStatus(bookingRequestDTO.status());
        booking.setNumberOfPeople(bookingRequestDTO.numberOfPeople());
        booking.setStartTime(bookingRequestDTO.start());
        booking.setEndTime(bookingRequestDTO.end());
        booking.setCode(bookingRequestDTO.code());
        booking.setCreatedAt(OffsetDateTime.now());

        return booking;
    }

    public BookingResponseDTO toResponse(BookingEntity booking) {
        return new BookingResponseDTO(
                booking.getUserEntity().getId(),
                booking.getMeetingRoom().getCode(),
                booking.getCode(),
                booking.getStatus(),
                booking.getNumberOfPeople(),
                booking.getStartTime(),
                booking.getEndTime());
    }
}
