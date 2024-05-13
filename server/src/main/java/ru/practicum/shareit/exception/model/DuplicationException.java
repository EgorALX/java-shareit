package ru.practicum.shareit.exception.model;

public class DuplicationException extends RuntimeException {
    public DuplicationException(String message) {
        super(message);
    }
}
