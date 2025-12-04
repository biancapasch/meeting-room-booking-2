package com.biancapasch.meetingroombooking2.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank(message = "nome é obrigatório")
        String name,

        @NotBlank(message = "email é obrigatório")
        String email
) {
}
