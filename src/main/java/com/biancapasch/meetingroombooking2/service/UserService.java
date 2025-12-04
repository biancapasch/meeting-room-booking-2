package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.domain.exceptions.UserNotFoundException;
import com.biancapasch.meetingroombooking2.dtos.UserPatchRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import com.biancapasch.meetingroombooking2.mapper.UserMapper;
import com.biancapasch.meetingroombooking2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        UserEntity entity = mapper.toEntity(userRequestDTO);

        UserEntity saved = repository.save(entity);

        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        UserEntity user = findById(id);
        repository.delete(user);
    }

    public UserEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserPatchRequestDTO req) {
        UserEntity user = repository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario não encontrado"));

        if (req.name() != null) {
            user.setName(req.name());
        }
        if (req.email() != null) {
            user.setEmail(req.email());
        }
        user.setUpdatedAt(OffsetDateTime.now());

        repository.save(user);

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public UserResponseDTO findByName(String name) {
        UserEntity u = repository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        return mapper.toResponse(u);
    }
}
