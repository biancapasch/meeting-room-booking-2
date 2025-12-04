package com.biancapasch.meetingroombooking2.domain.exceptions;

public class InactiveMeetingRoomException extends RuntimeException {

    public InactiveMeetingRoomException(String message) {
        super(message);
    }
}
