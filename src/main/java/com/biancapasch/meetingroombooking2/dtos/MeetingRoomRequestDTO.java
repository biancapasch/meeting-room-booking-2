package com.biancapasch.meetingroombooking2.dtos;

import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

public record MeetingRoomRequestDTO(
        @NotNull(message = "code é obrigatório") @Positive
        Long code,

        @NotBlank(message = "nome é obrigatório")
        String name,

        @NotNull(message = "capacity é obrigatório")
        @Positive(message = "capacity deve ser > 0")
        Integer capacity,

        MeetingRoomStatus status
) {
}
