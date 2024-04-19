package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State convertStateStringToEnum(String state) {
        try {
            return State.valueOf(state);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}