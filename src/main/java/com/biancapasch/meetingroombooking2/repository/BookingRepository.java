package com.biancapasch.meetingroombooking2.repository;

import com.biancapasch.meetingroombooking2.domain.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query("""
    select (count(b) > 0)
    from BookingEntity b
    where b.meetingRoom.id = :roomId
      and b.status = com.biancapasch.meetingroombooking2.domain.enums.BookingStatus.ACTIVE
      and b.startTime < :end
      and b.endTime   > :start
    """)
    boolean existsOverlap(@Param("roomId") Long roomId,
                          @Param("start") OffsetDateTime start,
                          @Param("end") OffsetDateTime end);

}
