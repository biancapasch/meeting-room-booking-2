package com.biancapasch.meetingroombooking2.dtos;

import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.OffsetDateTime;

public record BookingRequestDTO(
        @NotNull(message = "userId não pode ser nulo")
        Long userId,

        @NotNull(message = "meetingRoomCode não pode ser nulo")
        Long meetingRoomCode,

        @NotNull(message = "Code não pode ser nulo")
        Long code,

        BookingStatus status,

        @Positive(message = "NumberOfPeople precisa ser positivo")
        @NotNull(message = "NumberOfPeople não pode ser nulo")
        Integer numberOfPeople,

        @NotNull(message = "Start não pode ser nulo")
        OffsetDateTime start,

        @NotNull(message = "End não pode ser nulo")
        OffsetDateTime end
) {
}
