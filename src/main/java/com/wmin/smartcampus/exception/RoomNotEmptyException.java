package com.wmin.smartcampus.exception;

/**
 * Thrown when a client attempts to DELETE a Room that still has
 * active sensors linked to it.
 * Mapped → HTTP 409 Conflict
 */
public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;

    public RoomNotEmptyException(String roomId) {
        super("Room '" + roomId + "' cannot be deleted: it still has sensors assigned to it.");
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
}
