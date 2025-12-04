package com.biancapasch.meetingroombooking2.domain.exceptions;

public class MeetingRoomNotFoundException extends RuntimeException {

    public MeetingRoomNotFoundException(String message) {
        super(message);
    }
}
