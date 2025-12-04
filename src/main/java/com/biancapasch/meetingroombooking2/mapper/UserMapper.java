package com.biancapasch.meetingroombooking2.mapper;

import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.dtos.UserRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequestDTO userRequestDTO) {
        UserEntity u = new UserEntity();

        u.setName(userRequestDTO.name());
        u.setEmail(userRequestDTO.email());

        return u;
    }

    public UserResponseDTO toResponse(UserEntity u) {
        return new UserResponseDTO(
                u.getId(),
                u.getName(),
                u.getEmail()
        );
    }
}
