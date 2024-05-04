package ru.practicum.shareit.booking.enums;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State stateValueOf(String state) {
        for (State s : State.values()) {
            if (s.name().equalsIgnoreCase(state)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}