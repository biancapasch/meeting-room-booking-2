package com.biancapasch.meetingroombooking2.domain.controller;

import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import com.biancapasch.meetingroombooking2.service.MeetingRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService service;

    @PostMapping
    public ResponseEntity<MeetingRoomResponseDTO> create(@Valid @RequestBody MeetingRoomRequestDTO req) {
        MeetingRoomResponseDTO resp = service.createMeetingRoom(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    @GetMapping("/{code}")
    public ResponseEntity<MeetingRoomResponseDTO> findByCode(@PathVariable Long code) {
        MeetingRoomResponseDTO resp = service.findByCodeAndReturnDTO(code);

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete (@PathVariable Long code) {
        service.deleteByCode(code);

        return ResponseEntity.noContent().build();
    }
}
