package com.biancapasch.meetingroombooking2.mapper;

import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomRequestDTO;
import com.biancapasch.meetingroombooking2.dtos.MeetingRoomResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MeetingRoomMapper {

    public MeetingRoomEntity toEntity(MeetingRoomRequestDTO req) {
        MeetingRoomEntity m = new MeetingRoomEntity();
        m.setCode(req.code());
        m.setName(req.name());
        m.setCapacity(req.capacity());
        m.setStatus(req.status());

        return m;
    }

    public MeetingRoomResponseDTO toResponse(MeetingRoomEntity entity) {
        return new MeetingRoomResponseDTO(
                entity.getCode(),
                entity.getName(),
                entity.getCapacity(),
                entity.getStatus()
        );
    }
}
