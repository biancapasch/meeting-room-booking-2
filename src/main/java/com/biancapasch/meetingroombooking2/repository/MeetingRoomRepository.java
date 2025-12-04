package com.biancapasch.meetingroombooking2.repository;

import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoomEntity, Long> {

    Optional<MeetingRoomEntity> findByCode(Long code);
}
