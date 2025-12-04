package com.biancapasch.meetingroombooking2.domain.entity;

import com.biancapasch.meetingroombooking2.domain.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserEntity userEntity;

    @ManyToOne
    private MeetingRoomEntity meetingRoom;

    @Column(nullable = false)
    private Long code;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(nullable = false)
    @Positive
    private Integer numberOfPeople;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    @Column(nullable = false)

    private OffsetDateTime endTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}

