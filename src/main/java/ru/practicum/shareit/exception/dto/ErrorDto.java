package ru.practicum.shareit.exception.dto;

import lombok.Getter;

@Getter
public class ErrorDto {

    private final String message;

    private final String stackTrace;

    public ErrorDto(String message) {
        this.message = message;
        stackTrace = null;
    }

    public ErrorDto(String message, String error) {
        this.message = message;
        this.stackTrace = error;
    }
}
