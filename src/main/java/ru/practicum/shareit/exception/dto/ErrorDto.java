package ru.practicum.shareit.exception.dto;

import lombok.Getter;

@Getter
public class ErrorDto {

    private final String error;

    private final String stackTrace;

    public ErrorDto(String message) {
        this.error = message;
        stackTrace = null;
    }

    public ErrorDto(String message, String stackTrace) {
        this.error = message;
        this.stackTrace = stackTrace;
    }
}
