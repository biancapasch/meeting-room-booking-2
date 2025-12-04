package com.biancapasch.meetingroombooking2.dtos;

import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;

import java.time.OffsetDateTime;

public record BookingResponseDTO(
        Long userId,
        Long meetingRoomCode,
        Long code,
        BookingStatus status,
        Integer numberOfPeople,
        OffsetDateTime start,
        OffsetDateTime end
) {
}
