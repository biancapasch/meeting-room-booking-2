package com.biancapasch.meetingroombooking2.repository;

import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@DataJpaTest
class MeetingRoomRepositoryTest {

    @Autowired
    private MeetingRoomRepository repository;

    private MeetingRoomEntity newRoom(Long code, String name, int capacity, MeetingRoomStatus status) {
        var room = new MeetingRoomEntity();
        room.setCode(code);
        room.setName(name);
        room.setCapacity(capacity);
        room.setStatus(status);

        return room;
    }

    @Test
    void findAll_shouldReturnPersistedRooms() {
        MeetingRoomEntity r1 = repository.save(newRoom(100L, "Sala A", 10, MeetingRoomStatus.ACTIVE));
        MeetingRoomEntity r2 = repository.save(newRoom(101L, "Sala B", 8,  MeetingRoomStatus.INACTIVE));
        MeetingRoomEntity r3 = repository.save(newRoom(102L, "Sala C", 20, MeetingRoomStatus.ACTIVE));

        List<MeetingRoomEntity> all = repository.findAll();

        assertThat(all).hasSize(3);
        assertThat(all)
                .extracting(MeetingRoomEntity::getCode, MeetingRoomEntity::getName, MeetingRoomEntity::getCapacity, MeetingRoomEntity::getStatus)
                .containsExactlyInAnyOrder(
                        tuple(100L, "Sala A", 10, MeetingRoomStatus.ACTIVE),
                        tuple(101L, "Sala B", 8,  MeetingRoomStatus.INACTIVE),
                        tuple(102L, "Sala C", 20, MeetingRoomStatus.ACTIVE)
                );
        assertThat(all).allSatisfy(r -> {
            assertThat(r.getId()).isNotNull();
            assertThat(r.getCreatedAt()).isNotNull();
            assertThat(r.getUpdatedAt()).isNotNull();
        });
    }

    @Test
    void findByCode_shouldReturnRoom_whenExists() {
        repository.save(newRoom(200L, "Sala X", 12, MeetingRoomStatus.ACTIVE));

        Optional<MeetingRoomEntity> found = repository.findByCode(200L);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Sala X");
        assertThat(found.get().getCapacity()).isEqualTo(12);
    }

    @Test
    void findByCode_shouldBeEmpty_whenNotExists() {
        assertThat(repository.findByCode(999L)).isEmpty();
    }

}
