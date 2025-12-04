package com.biancapasch.meetingroombooking2.repository;

import com.biancapasch.meetingroombooking2.domain.entity.BookingEntity;
import com.biancapasch.meetingroombooking2.domain.entity.MeetingRoomEntity;
import com.biancapasch.meetingroombooking2.domain.entity.UserEntity;
import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;
import com.biancapasch.meetingroombooking2.domain.enums.MeetingRoomStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @Autowired
    private UserRepository userRepository;

    private MeetingRoomEntity newRoom(Long code) {
        var now = OffsetDateTime.now();
        var r = new MeetingRoomEntity(
                null,
                code,
                "Sala " + code,
                10,
                MeetingRoomStatus.ACTIVE,
                now,
                now
        );
        return meetingRoomRepository.save(r);
    }

    private UserEntity newUser(String name, String email) {
        var u = new UserEntity();
        u.setName(name);
        u.setEmail(email);
        return userRepository.save(u);
    }

    private BookingEntity newBooking(MeetingRoomEntity room,
                                     UserEntity user,
                                     OffsetDateTime start,
                                     OffsetDateTime end,
                                     BookingStatus status) {
        var b = new BookingEntity();
        b.setMeetingRoom(room);
        b.setUserEntity(user);      // ✅ casa com o campo da entidade
        b.setCode(1L);              // ✅ não pode ser null (nullable = false)
        b.setStartTime(start);
        b.setEndTime(end);
        b.setNumberOfPeople(2);
        b.setStatus(status);
        return bookingRepository.save(b);
    }

    @Test
    void shouldReturnTrueWhenIntervalsOverlapInside() {
        var room = newRoom(100L);
        var u = newUser("Bianca", "bianca@me.com");

        var s1 = OffsetDateTime.now().plusHours(1);
        var e1 = s1.plusHours(2);
        newBooking(room, u, s1, e1, BookingStatus.ACTIVE);

        var ns = s1.plusMinutes(30);
        var ne = ns.plusHours(1);

        boolean conflict = bookingRepository.existsOverlap(room.getId(), ns, ne);
        assertThat(conflict).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBackToBackNoOverlapByRule() {
        var room = newRoom(101L);
        var u = newUser("B", "b@me.com");

        var s1 = OffsetDateTime.now().plusHours(2);
        var e1 = s1.plusHours(2);
        newBooking(room, u, s1, e1, BookingStatus.ACTIVE);

        var ns = e1;               // começa exatamente quando o outro termina
        var ne = ns.plusHours(1);

        boolean conflict = bookingRepository.existsOverlap(room.getId(), ns, ne);
        assertThat(conflict).isFalse();
    }

    @Test
    void shouldReturnFalseWhenDifferentRoom() {
        var r1 = newRoom(200L);
        var r2 = newRoom(201L);
        var u = newUser("C", "c@me.com");

        var s1 = OffsetDateTime.now().plusHours(3);
        var e1 = s1.plusHours(1);
        newBooking(r1, u, s1, e1, BookingStatus.ACTIVE);

        boolean conflict = bookingRepository.existsOverlap(
                r2.getId(),
                s1.plusMinutes(10),
                e1.plusMinutes(10)
        );
        assertThat(conflict).isFalse();
    }

    @Test
    void shouldIgnoreCancelledBookingsWhenCheckingOverlap() {
        var room = newRoom(300L);
        var u = newUser("D", "d@me.com");

        var s1 = OffsetDateTime.now().plusHours(4);
        var e1 = s1.plusHours(2);
        newBooking(room, u, s1, e1, BookingStatus.CANCELLED);

        boolean conflict = bookingRepository.existsOverlap(
                room.getId(),
                s1.plusMinutes(30),
                e1.minusMinutes(30)
        );
        assertThat(conflict).isFalse();
    }
}
