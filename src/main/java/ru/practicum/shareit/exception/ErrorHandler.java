package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.dto.ErrorDto;
import ru.practicum.shareit.exception.model.DuplicationException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.exception.model.AccessException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFoundException(final NotFoundException exception) {
        log.info("Данные не найдены {}", exception.getMessage());
        String message = "Данные не найдены " + exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleAccessException(final AccessException exception) {
        log.info("Ошибка доступа {}", exception.getMessage());
        String message = exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationException(final ValidationException exception) {
        log.info("Ошибка валидации {}", exception.getMessage());
        String message = "Validation error: " + exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleDuplicationException(final DuplicationException exception) {
        log.info("Ошибка дублирования данных: {}", exception.getMessage());
        String message = "Duplication error: " + exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.info("Ошибка уникальности: {}", exception.getMessage());
        String message = "DataIntegrityViolation error: " + exception.getMessage();
        return new ErrorDto(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(final Exception exception) {
        StringWriter error = new StringWriter();
        exception.printStackTrace(new PrintWriter(error));
        String message = "Exception: " + exception.getMessage() + " StackTrace: " + error.toString();
        log.error("Exception: ", exception);
        return new ErrorDto(exception.getMessage(), error.toString());
    }
}