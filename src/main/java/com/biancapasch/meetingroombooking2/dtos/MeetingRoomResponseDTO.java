package com.biancapasch.meetingroombooking2.dtos;

import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;

public record MeetingRoomResponseDTO(
        Long code,
        String name,
        Integer capacity,
        MeetingRoomStatus status
) {
}
