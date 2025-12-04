package com.biancapasch.meetingroombooking2.service;

import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import com.biancapasch.meetingroombooking2.domain.exceptions.MeetingRoomNotFoundException;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import com.biancapasch.meetingroombooking2.mapper.MeetingRoomMapper;
import com.biancapasch.meetingroombooking2.repository.MeetingRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository repository;
    private final MeetingRoomMapper mapper;

    @Transactional
    public MeetingRoomResponseDTO createMeetingRoom(MeetingRoomRequestDTO meetingRoomRequestDTO) {
        MeetingRoomEntity mr = mapper.toEntity(meetingRoomRequestDTO);

        if (mr.getStatus() == null) {
            mr.setStatus(MeetingRoomStatus.ACTIVE);
        }

        MeetingRoomEntity saved = repository.save(mr);

        return mapper.toResponse(saved);
    }

    public MeetingRoomEntity findByCode(Long code) {

        return repository
                .findByCode(code)
                .orElseThrow(() -> new MeetingRoomNotFoundException("MeetingRoom com " + code + " não encontrado"));
    }

    public MeetingRoomResponseDTO findByCodeAndReturnDTO(Long code) {
        MeetingRoomEntity meetingRoom = repository
                .findByCode(code)
                .orElseThrow(() -> new MeetingRoomNotFoundException("MeetingRoom com " + code + " não encontrado"));

        return mapper.toResponse(meetingRoom);
    }

    @Transactional
    public void deleteByCode(Long code) {
        MeetingRoomEntity mr = findByCode(code);

        repository.delete(mr);
    }

}
