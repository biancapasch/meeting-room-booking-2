package com.biancapasch.meetingroombooking2.domain.controller;

import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.dtos.UserPatchRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.UserResponseDTO;
import com.biancapasch.meetingroombooking2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO req) {
        UserResponseDTO resp = service.create(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                         @Valid @RequestBody UserPatchRequestDTO req) {
        UserResponseDTO resp = service.update(id, req);

        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getByName(String name) {
        UserResponseDTO resp = service.findByName(name);

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserEntity u = service.findById(id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}
